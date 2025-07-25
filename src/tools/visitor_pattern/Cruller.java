package tools.visitor_pattern;

public class Cruller extends Pastry {

    @Override
    public void accept(PastryVisitor visitor) {
        visitor.visitCruller(this);
    }

    @Override
    public void delete(PastryVisitor visitor) {
        visitor.deleteCruller(this);
    }
}
