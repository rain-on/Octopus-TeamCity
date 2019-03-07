package octopus.teamcity.agent;

import java.util.Arrays;
import java.util.List;

public class CommentParserFactory {
    private static final String JIRA_PARSER = "Jira";
    private static final String GITHUB_PARSER = "GitHub";

    // Make sure this list aligns with editOctopusPackPackage.jsp
    private static final List<String> parsers = Arrays.asList("", JIRA_PARSER, GITHUB_PARSER);

    public static List<String> getParsers() {
        return parsers;
    }

    public CommentParser getParser(String parser) throws Exception {
        switch (parser) {
            case JIRA_PARSER:
                return new JiraCommentParser();
            case GITHUB_PARSER:
                return new GitHubCommentParser();
        }
        throw new Exception("Unsupported parser value " + parser);
    }
}
