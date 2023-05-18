package models;

public class IssueTransition {

    private final Transition transition;

    public IssueTransition(Transition transition) {
        this.transition = transition;
    }

    public Transition getTransition() {
        return transition;
    }

    public static class Transition{
        private final String id;

        public Transition(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
