package com.example.datn_sevenstrike.ghn.service;

import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.ghn.config.GhnConfig;
import com.example.datn_sevenstrike.ghn.dto.request.GhnTinhPhiRequest;
import com.example.datn_sevenstrike.ghn.dto.response.GhnDistrictResponse;
import com.example.datn_sevenstrike.ghn.dto.response.GhnProvinceResponse;
import com.example.datn_sevenstrike.ghn.dto.response.GhnTinhPhiResponse;
import com.example.datn_sevenstrike.ghn.dto.response.GhnWardResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GhnService {

    private final RestTemplate ghnRestTemplate;
    private final GhnConfig.GhnProperties props;
    private final ObjectMapper objectMapper;

    // cache địa chỉ shop GHN
    private static final long SHOP_ADDR_TTL_MS = 10 * 60 * 1000L;

    private volatile long shopAddrCachedAt = 0L;
    private volatile Integer cachedFromDistrictId = null;
    private volatile String cachedFromWardCode = null;
    private volatile String cachedShopAddressText = null;

    // =========================
    // API MASTER DATA
    // =========================

    public List<GhnProvinceResponse> layDanhSachTinhThanh() {
        String url = apiUrl("/shiip/public-api/master-data/province");
        JsonNode data = callGhnGet(url, false).path("data");
        if (!data.isArray()) return Collections.emptyList();

        List<GhnProvinceResponse> out = new ArrayList<>();
        for (JsonNode n : data) {
            Integer id = asInt(n.get("ProvinceID"));
            String name = asText(n.get("ProvinceName"));
            if (id != null && name != null) {
                out.add(new GhnProvinceResponse(id, name));
            }
        }
        return out;
    }

    public List<GhnDistrictResponse> layDanhSachQuanHuyen(Integer provinceId) {
        if (provinceId == null || provinceId <= 0) {
            throw new BadRequestEx("Tỉnh/Thành phố không hợp lệ.");
        }

        String url = apiUrl("/shiip/public-api/master-data/district?province_id=" + provinceId);
        JsonNode data = callGhnGet(url, false).path("data");
        if (!data.isArray()) return Collections.emptyList();

        List<GhnDistrictResponse> out = new ArrayList<>();
        for (JsonNode n : data) {
            Integer id = asInt(n.get("DistrictID"));
            String name = asText(n.get("DistrictName"));
            Integer pId = asInt(n.get("ProvinceID"));
            if (id != null && name != null) {
                out.add(new GhnDistrictResponse(id, name, pId));
            }
        }
        return out;
    }

    public List<GhnWardResponse> layDanhSachPhuongXa(Integer districtId) {
        if (districtId == null || districtId <= 0) {
            throw new BadRequestEx("Quận/Huyện không hợp lệ.");
        }

        String url = apiUrl("/shiip/public-api/master-data/ward?district_id=" + districtId);
        JsonNode data = callGhnGet(url, false).path("data");
        if (!data.isArray()) return Collections.emptyList();

        List<GhnWardResponse> out = new ArrayList<>();
        for (JsonNode n : data) {
            String code = asText(n.get("WardCode"));
            String name = asText(n.get("WardName"));
            Integer dId = asInt(n.get("DistrictID"));
            if (code != null && name != null) {
                out.add(new GhnWardResponse(code, name, dId));
            }
        }
        return out;
    }

    // =========================
    // AUTO LẤY ĐỊA CHỈ SHOP
    // =========================

    public Map<String, Object> layDiaChiShopMacDinh() {
        validateConfig();

        FromAddress from = resolveFromAddress(null, null);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("shopId", props.getShopId());
        out.put("fromDistrictId", from.getFromDistrictId());
        out.put("fromWardCode", from.getFromWardCode());
        out.put("shopAddress", cachedShopAddressText);
        return out;
    }

    private FromAddress resolveFromAddress(Integer overrideDistrictId, String overrideWardCode) {
        // 1) ưu tiên request truyền lên
        Integer reqDistrictId = (overrideDistrictId != null && overrideDistrictId > 0) ? overrideDistrictId : null;
        String reqWardCode = safeTrim(overrideWardCode);
        if (reqDistrictId != null && reqWardCode != null) {
            return new FromAddress(reqDistrictId, reqWardCode);
        }

        // 2) ưu tiên application.properties nếu có cấu hình sẵn
        Integer propDistrictId = (props.getFromDistrictId() != null && props.getFromDistrictId() > 0)
                ? props.getFromDistrictId()
                : null;
        String propWardCode = safeTrim(props.getFromWardCode());
        if (propDistrictId != null && propWardCode != null) {
            return new FromAddress(propDistrictId, propWardCode);
        }

        // 3) lấy từ cache
        long now = System.currentTimeMillis();
        if (cachedFromDistrictId != null && cachedFromDistrictId > 0
                && cachedFromWardCode != null && !cachedFromWardCode.isBlank()
                && shopAddrCachedAt > 0
                && (now - shopAddrCachedAt) < SHOP_ADDR_TTL_MS) {
            return new FromAddress(cachedFromDistrictId, cachedFromWardCode);
        }

        // 4) gọi GHN shop/all để tự lấy
        synchronized (this) {
            now = System.currentTimeMillis();

            if (cachedFromDistrictId != null && cachedFromDistrictId > 0
                    && cachedFromWardCode != null && !cachedFromWardCode.isBlank()
                    && shopAddrCachedAt > 0
                    && (now - shopAddrCachedAt) < SHOP_ADDR_TTL_MS) {
                return new FromAddress(cachedFromDistrictId, cachedFromWardCode);
            }

            FromAddress fresh = fetchFromAddressFromShopAll();

            cachedFromDistrictId = fresh.getFromDistrictId();
            cachedFromWardCode = fresh.getFromWardCode();
            shopAddrCachedAt = now;

            // optional: gán ngược vào props để dùng lại
            props.setFromDistrictId(cachedFromDistrictId);
            props.setFromWardCode(cachedFromWardCode);

            return fresh;
        }
    }

    private FromAddress fetchFromAddressFromShopAll() {
        String url = apiUrl("/shiip/public-api/v2/shop/all");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("offset", 0);
        body.put("limit", 50);
        body.put("client_phone", "");

        JsonNode root = callGhnPost(url, body, false);
        JsonNode shops = root.path("data").path("shops");

        if (!shops.isArray() || shops.size() == 0) {
            throw new BadRequestEx("GHN không trả về danh sách shop.");
        }

        Integer targetShopId = props.getShopId();
        JsonNode chosen = null;

        if (targetShopId != null && targetShopId > 0) {
            for (JsonNode s : shops) {
                Integer id = asInt(s.get("_id"));
                if (id != null && id.equals(targetShopId)) {
                    chosen = s;
                    break;
                }
            }
        }

        if (chosen == null) {
            chosen = shops.get(0);
        }

        Integer districtId = asInt(chosen.get("district_id"));
        String wardCode = asText(chosen.get("ward_code"));
        cachedShopAddressText = asText(chosen.get("address"));

        if (districtId == null || districtId <= 0) {
            throw new BadRequestEx("Không lấy được district_id của shop từ GHN.");
        }
        if (wardCode == null || wardCode.isBlank()) {
            throw new BadRequestEx("Không lấy được ward_code của shop từ GHN.");
        }

        return new FromAddress(districtId, wardCode);
    }

    // =========================
    // TÍNH PHÍ VẬN CHUYỂN
    // =========================

    public GhnTinhPhiResponse tinhPhiVanChuyen(GhnTinhPhiRequest req) {
        validateConfig();

        Integer toDistrictId = req.getToDistrictId();
        String toWardCode = safeTrim(req.getToWardCode());

        if (toDistrictId == null || toDistrictId <= 0) {
            throw new BadRequestEx("Thiếu mã Quận/Huyện (GHN) của nơi nhận.");
        }
        if (toWardCode == null || toWardCode.isBlank()) {
            throw new BadRequestEx("Thiếu mã Phường/Xã (GHN) của nơi nhận.");
        }

        FromAddress from = resolveFromAddress(req.getFromDistrictId(), req.getFromWardCode());
        Integer fromDistrictId = from.getFromDistrictId();
        String fromWardCode = from.getFromWardCode();

        int weight = normalizePositive(req.getTongCanNang(), 1200);
        int length = normalizePositive(req.getDai(), 30);
        int width = normalizePositive(req.getRong(), 20);
        int height = normalizePositive(req.getCao(), 10);

        long tongGiaTriHang = req.getTongGiaTriHang() != null ? Math.max(0, req.getTongGiaTriHang()) : 0;

        Integer serviceId = req.getServiceId();
        if (serviceId == null || serviceId <= 0) {
            serviceId = pickServiceId(fromDistrictId, toDistrictId);
        }

        JsonNode feeNode = callTinhPhi(
                serviceId,
                fromDistrictId,
                fromWardCode,
                toDistrictId,
                toWardCode,
                weight,
                length,
                width,
                height,
                tongGiaTriHang
        );

        JsonNode data = feeNode.path("data");
        long total = asLong(data.get("total"), 0L);
        long serviceFee = asLong(data.get("service_fee"), 0L);
        long insuranceFee = asLong(data.get("insurance_fee"), 0L);

        return new GhnTinhPhiResponse(total, serviceId, serviceFee, insuranceFee, total);
    }

    private Integer pickServiceId(Integer fromDistrictId, Integer toDistrictId) {
        String url = apiUrl("/shiip/public-api/v2/shipping-order/available-services");

        Map<String, Object> body = new HashMap<>();
        body.put("shop_id", props.getShopId());
        body.put("from_district", fromDistrictId);
        body.put("to_district", toDistrictId);

        JsonNode root = callGhnPost(url, body, true);
        JsonNode data = root.path("data");

        if (!data.isArray() || data.size() == 0) {
            throw new BadRequestEx("GHN không trả về dịch vụ vận chuyển phù hợp cho tuyến đường này.");
        }

        Integer best = null;
        for (JsonNode n : data) {
            Integer sId = asInt(n.get("service_id"));
            Integer sType = asInt(n.get("service_type_id"));
            if (sId != null && sId > 0 && sType != null && sType == 2) {
                best = sId;
                break;
            }
        }

        if (best != null) return best;

        Integer first = asInt(data.get(0).get("service_id"));
        if (first == null || first <= 0) {
            throw new BadRequestEx("Không xác định được service_id từ GHN.");
        }
        return first;
    }

    private JsonNode callTinhPhi(
            Integer serviceId,
            Integer fromDistrictId,
            String fromWardCode,
            Integer toDistrictId,
            String toWardCode,
            int weight,
            int length,
            int width,
            int height,
            long tongGiaTriHang
    ) {
        String url = apiUrl("/shiip/public-api/v2/shipping-order/fee");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("service_id", serviceId);
        body.put("from_district_id", fromDistrictId);
        body.put("from_ward_code", fromWardCode);
        body.put("to_district_id", toDistrictId);
        body.put("to_ward_code", toWardCode);
        body.put("weight", weight);
        body.put("length", length);
        body.put("width", width);
        body.put("height", height);
        body.put("insurance_value", tongGiaTriHang);

        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", "Hàng hóa");
        item.put("quantity", 1);
        item.put("weight", weight);
        item.put("length", length);
        item.put("width", width);
        item.put("height", height);
        items.add(item);
        body.put("items", items);

        return callGhnPost(url, body, true);
    }

    // =========================
    // HTTP HELPERS
    // =========================

    private void validateConfig() {
        if (props.getToken() == null || props.getToken().isBlank()) {
            throw new BadRequestEx("Bạn chưa cấu hình ghn.token.");
        }
        if (props.getShopId() == null || props.getShopId() <= 0) {
            throw new BadRequestEx("Bạn chưa cấu hình ghn.shop-id.");
        }
        if (props.getBaseUrl() == null || props.getBaseUrl().isBlank()) {
            throw new BadRequestEx("Bạn chưa cấu hình ghn.base-url.");
        }
    }

    private String apiUrl(String path) {
        String base = props.getBaseUrl().trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return base + path;
    }

    private HttpHeaders buildHeaders(boolean includeShopId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", props.getToken());

        if (includeShopId) {
            headers.set("ShopId", String.valueOf(props.getShopId()));
            headers.set("ShopID", String.valueOf(props.getShopId()));
        }

        return headers;
    }

    private JsonNode callGhnGet(String url, boolean includeShopId) {
        try {
            HttpEntity<Void> entity = new HttpEntity<>(buildHeaders(includeShopId));
            ResponseEntity<String> res = ghnRestTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            String body = res.getBody() == null ? "" : res.getBody();
            JsonNode root = objectMapper.readTree(body);

            int code = root.path("code").asInt(200);
            if (code != 200) {
                String msg = root.path("message").asText("Gọi GHN thất bại.");
                throw new BadRequestEx("Gọi GHN thất bại: " + msg);
            }

            return root;
        } catch (HttpStatusCodeException e) {
            String raw = e.getResponseBodyAsString();
            throw new BadRequestEx("GHN GET lỗi: " + raw);
        } catch (BadRequestEx e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestEx("Không gọi được GHN. Vui lòng kiểm tra cấu hình Token/ShopId và mạng.");
        }
    }

    private JsonNode callGhnPost(String url, Object body, boolean includeShopId) {
        try {
            HttpEntity<Object> entity = new HttpEntity<>(body, buildHeaders(includeShopId));
            ResponseEntity<String> res = ghnRestTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            String raw = res.getBody() == null ? "" : res.getBody();
            JsonNode root = objectMapper.readTree(raw);

            int code = root.path("code").asInt(200);
            if (code != 200) {
                String msg = root.path("message").asText("Gọi GHN thất bại.");
                throw new BadRequestEx("Gọi GHN thất bại: " + msg);
            }

            return root;
        } catch (HttpStatusCodeException e) {
            String raw = e.getResponseBodyAsString();
            throw new BadRequestEx("GHN POST lỗi: " + raw);
        } catch (BadRequestEx e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestEx("Không gọi được GHN. Vui lòng kiểm tra cấu hình Token/ShopId và dữ liệu địa chỉ GHN.");
        }
    }

    private Integer asInt(JsonNode node) {
        if (node == null || node.isNull()) return null;
        if (node.isInt() || node.isLong()) return node.asInt();

        try {
            String s = node.asText();
            if (s == null || s.isBlank()) return null;
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private String asText(JsonNode node) {
        if (node == null || node.isNull()) return null;
        String s = node.asText();
        return s == null ? null : s.trim();
    }

    private long asLong(JsonNode node, long def) {
        if (node == null || node.isNull()) return def;
        if (node.isLong() || node.isInt()) return node.asLong();

        try {
            String s = node.asText();
            if (s == null || s.isBlank()) return def;
            return Long.parseLong(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    private int normalizePositive(Integer n, int def) {
        if (n == null || n <= 0) return def;
        return n;
    }

    private String safeTrim(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isEmpty() ? null : x;
    }

    private static class FromAddress {
        private final Integer fromDistrictId;
        private final String fromWardCode;

        public FromAddress(Integer fromDistrictId, String fromWardCode) {
            this.fromDistrictId = fromDistrictId;
            this.fromWardCode = fromWardCode;
        }

        public Integer getFromDistrictId() {
            return fromDistrictId;
        }

        public String getFromWardCode() {
            return fromWardCode;
        }
    }
}