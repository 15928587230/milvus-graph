package com.owinfo.milvus.document.neo4j.prompt;


/**
 * entity relation prompt sentence
 */
public class Prompt {

    // 对文本总结，生成文本属于的专业领域
    public static String general_domain_prompt = "你是一个智能助手，可以帮助人类分析文本文档中的信息。\n" +
            "给定一个示例文本，通过分配一个描述性领域来帮助用户，该领域总结了文本的内容。\n" +
            "示例领域包括：“社会研究”、“算法分析”、“医学科学”等。\n" +
            "\n" +
            "文本：{input_text}\n" +
            "领域：";

    public static String defaultTask = "识别利益共同体的关系和结构，特别是在 {domain} 领域内。";

    public static String general_persona_prompt = "您是帮助人类分析文本文档中信息的智能助手。\n" +
            "给定特定类型的任务和示例文本，通过生成 3 到 4 句专家描述来帮助用户，这些专家可以帮助解决问题。\n" +
            "使用类似于下面的格式：\n" +
            "您是专家 {{角色}}。您擅长 {{对应的技能}}。您擅长帮助人们完成 {{特定的任务}}。\n" +
            "\n" +
            "任务：{sample_task}\n" +
            "角色描述：";

    public static String entity_types_prompt = "目标是研究实体类型与其特征之间的联系和关系，以便从文本中了解所有可用信息。\n" +
            "用户的任务是 {task}。\n" +
            "作为分析的一部分，您想要识别以下文本中存在的实体类型。\n" +
            "实体类型必须与用户任务相关。\n" +
            "避免使用“其他”或“未知”等一般实体类型。\n" +
            "这非常重要：不要生成冗余或重叠的实体类型。例如，如果文本包含“公司”和“组织”实体类型，应该返回公司或者组织其中一种。\n" +
            "在保证质量的同时，生成\"不超过10个\"的实体类型。并确保答案中的所有内容都与实体提取的上下文相关。\n" +
            "请记住，我们需要的是实体类型。\n" +
            "将实体类型作为逗号分隔的字符串列表返回。\n" +
            "===========================================================================\n" +
            "示例部分：以下部分包含示例输出。这些示例**必须从您的答案中排除**。\n" +
            "\n" +
            "示例 1\n" +
            "任务：确定指定社区内的联系和组织层次结构。\n" +
            "文本：Example_Org_A 是一家瑞典公司。Example_Org_A 的主管是 Example_Individual_B。\n" +
            "响应：\n" +
            "组织，个人\n" +
            "示例 1 结束\n" +
            "\n" +
            "示例 2\n" +
            "任务：确定不同哲学流派之间共享的关键概念、原则和论点，并追溯它们对彼此的历史或意识形态影响。\n" +
            "文本：理性主义，以勒内·笛卡尔等思想家为代表，认为理性是知识的主要来源。该学派的关键概念包括强调演绎推理方法。\n" +
            "回应：\n" +
            "概念、人、思想流派\n" +
            "示例 2 结束\n" +
            "\n" +
            "示例 3\n" +
            "任务：确定间接影响问题的所有基本力量、因素和趋势。\n" +
            "文本：松下等行业领导者正在争夺电池生产领域的霸主地位。他们正在大力投资研发，并正在探索新技术以获得竞争优势。\n" +
            "响应：\n" +
            "组织、技术、部门、投资策略\n" +
            "示例 3 结束\n" +
            "============================================================================\n" +
            "\n" +
            "====================================================================================================\n" +
            "真实数据：以下部分是真实数据。您应该仅使用这些真实数据来准备答案。仅生成实体类型。\n" +
            "任务：{task}\n" +
            "文本：{input_text}\n" +
            "响应：\n" +
            "{{<实体类型>}}";

    public static String entityRelationshipsGenerationPrompt = "-目标-\n" +
            "给定一个可能与此活动相关的文本文档和实体类型列表，从文本中识别出这些类型的所有实体以及已识别实体之间的所有关系。\n" +
            "\n" +
            "-步骤-\n" +
            "1. 识别所有实体, 按照优先级提取不超过10个实体。对于每个已识别的实体，提取以下信息：\n" +
            "- entity_name：实体的名称\n" +
            "- entity_type：以下类型之一：[{entity_types}]\n" +
            "- entity_description：实体属性和活动的全面描述\n" +
            "格式化每个实体，在开头和结尾处添加括号，格式为 (\"entity\",\"<entity_name>\",\"<entity_type>\",\"<entity_description>\")\n" +
            "例如：(\"entity\",\"Microsoft\",\"organization\",\"Microsoft is a technology company\")\n" +
            "\n" +
            "2. 从步骤 1 识别的entity_name列表中，找出所有 *明显相关* 的 (source_entity name, target_entity name) 对。\n" +
            "对于每对相关实体，提取以下信息：\n" +
            "- source_entity：源实体的名称 entity_name，如步骤 1 中所述\n" +
            "- target_entity：目标实体的名称 entity_name，如步骤 1 中所述\n" +
            "- relationship_description：解释您认为源实体和目标实体相互关联的原因\n" +
            "- relationship_strength：1 到 10 之间的整数分数，表示源实体和目标实体之间的关系强度\n" +
            "格式化每个关系，在开头和结尾处添加括号，如 (\"relationship\",\"<source_entity>\",\"<target_entity>\",\"<relationship_description>\",\"<relationship_strength>\")\n" +
            "例如：(\"relationship\",\"公司 A\",\"个人 A\",\"公司 A 目前归个人 A 所有\", 8)\n" +
            "\n" +
            "3. 以 {language} 形式返回输出，作为步骤 1 和 2 中确定的所有实体和关系的单个列表。使用 **{{record_delimiter}}** 作为列表分隔符。如果您必须翻译，只需翻译描述，无需其他内容！\n" +
            "\n" +
            "4. 不要输出建议和其他内容，只按照下面的格式进行输出：\n" +
            "{\n" +
            "  \"entities\": [\n" +
            "    (\"entity\",\"<entity_name>\",\"<entity_type>\",\"<entity_description>\"),\n" +
            "    (\"entity\",\"<entity_name>\",\"<entity_type>\",\"<entity_description>\"),\n" +
            "    (\"entity\",\"<entity_name>\",\"<entity_type>\",\"<entity_description>\"),\n" +
            "    (\"entity\",\"<entity_name>\",\"<entity_type>\",\"<entity_description>\")\n" +
            "  ],\n" +
            "  \"relationships\": [\n" +
            "    (\"relationship\",\"<source_entity>\",\"<target_entity>\",\"<relationship_description>\",\"<relationship_strength>\"),\n" +
            "    (\"relationship\",\"<source_entity>\",\"<target_entity>\",\"<relationship_description>\",\"<relationship_strength>\"),\n" +
            "    (\"relationship\",\"<source_entity>\",\"<target_entity>\",\"<relationship_description>\",\"<relationship_strength>\"),\n" +
            "    (\"relationship\",\"<source_entity>\",\"<target_entity>\",\"<relationship_description>\",\"<relationship_strength>\")\n" +
            "  ]\n" +
            "}\n" +
            "\n" +
            "-真实数据-\n" +
            "######################\n" +
            "entity_types：{entity_types}\n" +
            "text：{input_text}\n" +
            "######################\n" +
            "输出：";
}
