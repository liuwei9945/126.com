package test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonFieldReplacer {
    public static void replaceFieldValue(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            JsonNode fieldValueNode = objectNode.get("fieldValue");

            // 如果fieldValue字段存在并且值包含_workcode，则进行替换
            if (fieldValueNode != null && fieldValueNode.isTextual()) {
                String fieldValue = fieldValueNode.asText();
                if (fieldValue.contains("_workcode")) {
                    fieldValue = fieldValue.replace("_workcode", "");
                    objectNode.put("fieldValue", fieldValue);
                }
            }

            // 递归处理子节点
            objectNode.fields().forEachRemaining(entry -> replaceFieldValue(entry.getValue()));
        } else if (node.isArray()) {
            // 递归处理数组中的每个元素
            node.forEach(JsonFieldReplacer::replaceFieldValue);
        }
    }

    public static void main(String[] args) throws Exception {
        String json = "{\"mainData\":[{\"fieldName\":\"sqr\",\"fieldValue\":\"18755166955_telephone\"},{\"fieldName\":\"sqrq\",\"fieldValue\":\"2023-12-01\"}],\"detailData\":[{\"tableDBName\":\"formtable_main_691_dt1\",\"workflowRequestTableRecords\":[{\"recordOrder\":0,\"workflowRequestTableFields\":[{\"fieldName\":\"rzjg\",\"fieldValue\":\"认证机构\"},{\"fieldName\":\"rzbh\",\"fieldValue\":\"认证编号\"},{\"fieldName\":\"rzmc\",\"fieldValue\":\"认证名称\"},{\"fieldName\":\"rzrq\",\"fieldValue\":\"认证日期\"},{\"fieldName\":\"fj\",\"fieldValue\":\"附件\"},{\"fieldName\":\"remark\",\"fieldValue\":\"备注\"}]}]},{\"tableDBName\":\"formtable_main_691_dt2\",\"workflowRequestTableRecords\":[{\"recordOrder\":0,\"workflowRequestTableFields\":[{\"fieldName\":\"cysl\",\"fieldValue\":\"采样数量\"},{\"fieldName\":\"hgsl\",\"fieldValue\":\"合格数量\"},{\"fieldName\":\"blsl\",\"fieldValue\":\"不良数量\"},{\"fieldName\":\"hgl\",\"fieldValue\":\"合格率\"},{\"fieldName\":\"jcjg\",\"fieldValue\":\"检测结果\"},{\"fieldName\":\"ypjcjl\",\"fieldValue\":\"样品决策结论\"},{\"fieldName\":\"bz\",\"fieldValue\":\"备注\"},{\"fieldName\":\"jcksrq\",\"fieldValue\":\"检测开始日期\"},{\"fieldName\":\"jcjsrq\",\"fieldValue\":\"检测结束日期\"}]}]},{\"tableDBName\":\"formtable_main_691_dt3\",\"workflowRequestTableRecords\":[{\"recordOrder\":0,\"workflowRequestTableFields\":[{\"fieldName\":\"kcdf\",\"fieldValue\":\"考察得分\"},{\"fieldName\":\"kcjl\",\"fieldValue\":\"考察结论\"},{\"fieldName\":\"bz\",\"fieldValue\":\"备注\"}]}]}],\"remark\":\"自定义接口测试\",\"requestName\":\"企业签署\",\"workflowId\":\"49\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        replaceFieldValue(jsonNode);

        String updatedJson = objectMapper.writeValueAsString(jsonNode);
        System.out.println(updatedJson);
    }
}