package builder;

import models.IssueField;

public class IssueContentBuilder {

    private IssueField issueField;

    public  String build(String projectKey,String taskIssueType,String summary){
        IssueField.IssueType issueType = new IssueField.IssueType(taskIssueType);
        IssueField.Project project = new IssueField.Project(projectKey);
        IssueField.Fields fields = new IssueField.Fields(project, issueType, summary);
        issueField = new IssueField(fields);
        return BodyJSONBuilder.getJSONContent(issueField);
    }

    public IssueField getIssueField() {
        return issueField;
    }
}
