package com.owinfo.milvus.document.neo4j;

import com.owinfo.milvus.MilvusGraphApplicationTests;
import com.owinfo.milvus.document.LLMUtils;
import com.owinfo.milvus.document.neo4j.prompt.Prompt;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.qianfan.QianfanChatModel;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.neo4j.driver.*;

import java.io.IOException;
import java.util.List;

// 构建知识图谱测试
public class DocumentNeo4jTests extends MilvusGraphApplicationTests {

    private final String url = "bolt://192.168.0.120:7687";
    private final String username = "neo4j";
    private final String password = "123456Pjj";

    private Driver getNeo4jDriver() {
        return GraphDatabase.driver(url, AuthTokens.basic(username, password));
    }

    private Session getNeo4jSession() {
        return getNeo4jDriver().session();
    }

    @Test
    public void qianfanChat() {
        String answer = LLMUtils.qianfan().generate("您好，你可以提取实体关系吗");
        System.out.println(answer);
    }

    @Test
    public void getNeo4JConnection() {
        Driver driver = GraphDatabase.driver(url, AuthTokens.basic(password, password));
        ExecutableQuery executableQuery = driver.executableQuery("CALL db.labels()");
        EagerResult execute = executableQuery.execute();
        execute.records().forEach(System.out::println);
    }

    // 生成文本相关领域
    // 领域：体育教育与健康促进
    @Test
    public void getDomain() {
        String doc = Prompt.general_domain_prompt.replace("{input_text}", this.doc);
        String domain = LLMUtils.qianfan().generate(doc);
        System.out.println(domain);
    }

    // 获取该领域下面的角色信息
    // 您是专家体育教育与健康促进关系分析师。
    // 您擅长分析体育教育和健康促进领域内不同利益相关者的关系和结构。
    // 您擅长帮助人们完成利益相关者分析，识别关键的合作伙伴、潜在的冲突和合作机会。
    @Test
    public void getPersona() {
        String sampleTask = Prompt.defaultTask.replace("{domain}", "领域：体育教育与健康促进");
        String personaText = Prompt.general_persona_prompt.replace("{sample_task}", sampleTask);
        String persona = LLMUtils.qianfan().generate(personaText);
        System.out.println(persona);
    }

    // 生成的实体类型在下面entityTypes中
    @Test
    public void getEntityTypes() {
        String sampleTask = Prompt.defaultTask.replace("{domain}", "领域：体育教育与健康促进");
        String entityTypeText = Prompt.entity_types_prompt.replace("{task}", sampleTask).replace("{task}", sampleTask).replace("{input_text}", doc);
        String entityTypes = LLMUtils.qianfan().generate(entityTypeText);
        System.out.println(entityTypes);
    }

    @Test
    public void getEntityRelationships() {
        String entityRelationText = Prompt.entityRelationshipsGenerationPrompt.replace("{entity_types}", entityTypes).replace("{input_text}", doc);
        String entityRelationships = LLMUtils.qianfan().generate(entityRelationText);
        System.out.println(entityRelationships);
    }

    /**
     * GraphRAG知识抽取内容质量低，耗时长，需要的资源高，且耗时耗人力。
     * 因此可以采用折中方式，传统RAG + LLM文本处理，提高内容质量
     *
     * @throws IOException
     */
    @Test
    public void getDocEntityRelations() throws IOException {
        List<TextSegment> segments = LLMUtils.createSegment("中华人民共和国体育法.docx");
        StringBuilder res = new StringBuilder();
        QianfanChatModel qianfan = LLMUtils.qianfan();

        for (TextSegment segment : segments) {
            try {
                // 领域生成
                String doc = Prompt.general_domain_prompt.replace("{input_text}", segment.text());
                String domain = qianfan.generate(doc);

                // 生成领域下的实体类型列表
                String sampleTask = Prompt.defaultTask.replace("{domain}", domain);
                String entityTypeText = Prompt.entity_types_prompt.replace("{task}", sampleTask).replace("{task}", sampleTask).replace("{input_text}", segment.text());
                String entityTypes = LLMUtils.qianfan().generate(entityTypeText);
                // 防止文档内容过短导致domain、entityTypes获取失败
                if (StringUtils.isBlank(entityTypes) || entityTypes.length() < 12) continue;

                // 通过实体类型生成实体、关系
                String entityRelationText = Prompt.entityRelationshipsGenerationPrompt.replace("{entity_types}", entityTypes).replace("{input_text}", segment.text());
                String entityRelationships = LLMUtils.qianfan().generate(entityRelationText);
                res.append(entityRelationships).append("\n");
                Thread.sleep(1000);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        System.out.println(res);
    }

    String entityRelationShips = "{\n" +
            "  \"entities\": [\n" +
            "    (\"entity\", \"教育行政部门\", \"教育行政部门\", \"负责教育政策的制定和执行\"),\n" +
            "    (\"entity\", \"学校\", \"学校\", \"提供教育服务的机构\"),\n" +
            "    (\"entity\", \"体育行政部门\", \"体育行政部门\", \"负责体育政策的制定和执行\"),\n" +
            "    (\"entity\", \"国家\", \"国家\", \"主权国家\"),\n" +
            "    (\"entity\", \"体育课\", \"体育课\", \"学校开设的体育教学课程\"),\n" +
            "    (\"entity\", \"体育场地设施\", \"体育场地设施\", \"用于体育活动的场所和设施\"),\n" +
            "    (\"entity\", \"学生\", \"学生\", \"在学校接受教育的人\"),\n" +
            "    (\"entity\", \"学生体质健康标准\", \"学生体质健康标准\", \"评价学生健康状况的标准\"),\n" +
            "    (\"entity\", \"体育锻炼习惯\", \"体育锻炼习惯\", \"定期进行体育锻炼的习惯\"),\n" +
            "    (\"entity\", \"体育素养\", \"体育素养\", \"关于体育的知识和能力\"),\n" +
            "    (\"entity\", \"体育知识技能\", \"体育知识技能\", \"关于体育的专业知识和技术\"),\n" +
            "    (\"entity\", \"体育训练\", \"体育训练\", \"为了提高体育水平而进行的训练\"),\n" +
            "    (\"entity\", \"体育赛事活动\", \"体育赛事活动\", \"体育比赛和相关活动\"),\n" +
            "    (\"entity\", \"运动队\", \"运动队\", \"参加体育比赛的团队\"),\n" +
            "    (\"entity\", \"高水平运动队\", \"高水平运动队\", \"具有较高竞技水平的运动队\"),\n" +
            "    (\"entity\", \"体育课时\", \"体育课时\", \"体育课的上课时间\"),\n" +
            "    (\"entity\", \"病残等特殊体质学生\", \"特殊体质学生\", \"身体状况特殊的学生\"),\n" +
            "    (\"entity\", \"体育活动\", \"体育活动\", \"各种体育活动\")\n" +
            "  ],\n" +
            "  \"relationships\": [\n" +
            "    (\"relationship\", \"教育行政部门\", \"学校\", \"教育行政部门指导学校教育工作\", 8),\n" +
            "    (\"relationship\", \"体育行政部门\", \"学校\", \"体育行政部门为学校提供体育指导和帮助\", 7),\n" +
            "    (\"relationship\", \"学校\", \"体育课\", \"学校开设体育课\", 9),\n" +
            "    (\"relationship\", \"学校\", \"体育场地设施\", \"学校使用体育场地设施\", 8),\n" +
            "    (\"relationship\", \"学校\", \"学生\", \"学校教育学生\", 9),\n" +
            "    (\"relationship\", \"学校\", \"学生体质健康标准\", \"学校根据学生体质健康标准评价学生\", 8),\n" +
            "    (\"relationship\", \"学校\", \"体育锻炼习惯\", \"学校培养学生的体育锻炼习惯\", 7),\n" +
            "    (\"relationship\", \"学校\", \"体育素养\", \"学校提升学生的体育素养\", 7),\n" +
            "    (\"relationship\", \"学校\", \"体育知识技能\", \"学校传授体育知识技能\", 8),\n" +
            "    (\"relationship\", \"学校\", \"体育训练\", \"学校组织体育训练\", 8),\n" +
            "    (\"relationship\", \"学校\", \"体育赛事活动\", \"学校参与或举办体育赛事活动\", 7),\n" +
            "    (\"relationship\", \"学校\", \"运动队\", \"学校组建或支持运动队\", 7),\n" +
            "    (\"relationship\", \"学校\", \"高水平运动队\", \"学校可能拥有高水平运动队\", 5),\n" +
            "    (\"relationship\", \"学校\", \"体育课时\", \"学校确保体育课时的执行\", 9),\n" +
            "    (\"relationship\", \"学校\", \"病残等特殊体质学生\", \"学校为特殊体质学生安排合适的体育活动\", 7),\n" +
            "    (\"relationship\", \"学校\", \"体育活动\", \"学校组织各种体育活动\", 8)\n" +
            "  ]\n" +
            "}";

    String entityTypes = "实体类型：\n" +
            "1. 教育行政部门\n" +
            "2. 学校\n" +
            "3. 体育行政部门\n" +
            "4. 国家\n" +
            "5. 体育课\n" +
            "6. 体育场地设施\n" +
            "7. 学生\n" +
            "8. 学生体质健康标准\n" +
            "9. 体育锻炼习惯\n" +
            "10. 体育素养\n" +
            "11. 体育知识技能\n" +
            "12. 体育训练\n" +
            "13. 体育赛事活动\n" +
            "14. 运动队\n" +
            "15. 高水平运动队\n" +
            "16. 体育课时\n" +
            "17. 病残等特殊体质学生\n" +
            "18. 体育活动";

    String doc = "教育行政部门和学校应当将体育纳入学生综合素质评价范围，将达到国家学生体质健康标准要求作为教育教学考核的重要内容，培养学生体育锻炼习惯，提升学生体育素养。\n" +
            "体育行政部门应当在传授体育知识技能、组织体育训练、举办体育赛事活动、管理体育场地设施等方面为学校提供指导和帮助，并配合教育行政部门推进学校运动队和高水平运动队建设。\n" +
            "第二十六条　学校必须按照国家有关规定开齐开足体育课，确保体育课时不被占用。\n" +
            "学校应当在体育课教学时，组织病残等特殊体质学生参加适合其特点的体育活动。";

    @Test
    public void getLength() {
        // 长度234能生成这么多实体关系，上面限制300长度，估计返回内容又要限制token数量，导致格式错误
        System.out.println(doc.length());
    }

}
