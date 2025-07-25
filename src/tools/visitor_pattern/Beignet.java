package tools.visitor_pattern;

public class Beignet extends Pastry {
    @Override
    public void accept(PastryVisitor visitor) {
        visitor.visitBeignet(this);
    }

    @Override
    public void delete(PastryVisitor visitor) {
        visitor.deleteBeignet(this);
    }
}
