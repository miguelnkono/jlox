package tools.visitor_pattern;

public class Visit implements PastryVisitor{
    @Override
    public void visitBeignet(Beignet beignet) {
        System.out.println("we are visiting the Beignet pastry!!!");
    }

    @Override
    public void visitCruller(Cruller cruller) {
        System.out.println("we are visiting the Cruller pastry!!!");
    }

    @Override
    public void deleteBeignet(Beignet beignet) {
        System.out.println("deleting a Beigner!!!");
    }

    @Override
    public void deleteCruller(Cruller cruller) {
        System.out.println("deleting a Cruller!!!");
    }
}
