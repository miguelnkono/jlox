package tools.visitor_pattern;

public class App {
    public static void main(String[] args) {
        PastryVisitor pastryVisitor = new Visit();
        Beignet beignet = new Beignet();
        beignet.accept(pastryVisitor);
        beignet.delete(pastryVisitor);
        Cruller cruller = new Cruller();
        cruller.accept(pastryVisitor);
        cruller.delete(pastryVisitor);
    }
}
