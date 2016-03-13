package sjdb;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Estimator implements PlanVisitor {


	public Estimator() {
		// empty constructor
	}

	/* 
	 * Create output relation on Scan operator
	 *
	 * Example implementation of visit method for Scan operators.
	 */
	public void visit(Scan op) {
		Relation input = op.getRelation();
		Relation output = new Relation(input.getTupleCount());
		
		Iterator<Attribute> iter = input.getAttributes().iterator();
		while (iter.hasNext()) {
			output.addAttribute(new Attribute(iter.next()));
		}
		
		op.setOutput(output);
	}

	public void visit(Project op) {
		
		int value_count = 0;
		
		
		//visit the single child of a PROJECT operator
		op.getInput().accept(this);
		
		//Create a relation with the size of the biggest Attribute size
		List<Attribute> list = op.getAttributes();
		for(Attribute a : list){
			int att_val = a.getValueCount();
			if( att_val > value_count){
				value_count = att_val;
			}
		}
		Relation output = new Relation(value_count);
		
		//Add the attributes into the output Relation
		Iterator<Attribute> it = list.iterator();
		while(it.hasNext()){
			output.addAttribute(new Attribute(it.next()));
		}
		
		op.setOutput(output);
		
	}
	
	public void visit(Select op) {
		
		Attribute l_att,r_att;
		int l_val,r_val;
		Relation output = null;
		
		op.getInput().accept(this);
		
		Predicate pred = op.getPredicate();
		if(pred.equalsValue()){
			l_att = new Attribute(pred.getLeftAttribute().getName(), 1);
			output = new Relation(1);
			output.addAttribute(l_att);
		}else{
			l_att = new Attribute(pred.getLeftAttribute());
			r_att = new Attribute(pred.getRightAttribute());
			l_val = l_att.getValueCount();
			r_val = r_att.getValueCount();
			if(l_val < r_val){
				output = new Relation(l_val);
			}else{
				output = new Relation(r_val);
			}
			output.addAttribute(l_att);
		}
		
		op.setOutput(output);
	}
	
	public void visit(Product op) {
		Relation left_input = ((Scan) op.getLeft()).getRelation();
		Relation right_input = ((Scan) op.getRight()).getRelation();
		Relation output = new Relation(left_input.getTupleCount() * right_input.getTupleCount());
		
		List<Attribute> left_list = left_input.getAttributes();
		List<Attribute> right_list = right_input.getAttributes();
		
		for(Attribute a : left_list){
			output.addAttribute(new Attribute(a));
		}
		for(Attribute a : right_list){
			output.addAttribute(new Attribute(a));
		}
		
		op.setOutput(output);
	}
	
	public void visit(Join op) {
		Relation left_relation = op.getLeft().getOutput();
	}
}
