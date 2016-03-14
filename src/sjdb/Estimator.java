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
		
		Relation input = op.getInput().getOutput();
		Relation output = new Relation(input.getTupleCount());
		
		//Add project attributes contain in input's attributes to output relation
		Iterator<Attribute> iter = op.getAttributes().iterator();
		while (iter.hasNext()) {
			output.addAttribute(new Attribute(input.getAttribute(iter.next())));
		}
		
		op.setOutput(output);
	}
	
	public void visit(Select op) {
		
		Relation input = op.getInput().getOutput();
		Predicate pred = op.getPredicate();
		Attribute left = pred.getLeftAttribute();
		Relation output;
		
		//in attr=val form
		if(pred.equalsValue()){
			
			// T(R)/V(R,A)
			output = new Relation(input.getTupleCount()/input.getAttribute(left).getValueCount());
			
			// V(R',A)=1, other's kept same(smaller than T(R')
			for(Attribute a : input.getAttributes()){
				if(a.equals(left)){
					output.addAttribute(new Attribute(a.getName(),1));
				}else{
					output.addAttribute(new Attribute(a));
				}
			}
			
		//in attr=attr form	
		}else{
			Attribute right = pred.getRightAttribute();
			int left_val = input.getAttribute(left).getValueCount();
			int right_val = input.getAttribute(right).getValueCount();
			
			// T(R)/max( V(R,A), V(R,B) )
			// if a is bigger
			if(left_val > right_val){
				output = new Relation(input.getTupleCount()/left_val);
				
				//V(R',A) = V(R',B) = min( V(R,A), V(R,B) )
				for(Attribute a : input.getAttributes()){
					if(a.equals(left) || a.equals(right)){
						output.addAttribute(new Attribute(a.getName(),right_val));
					}else{
						output.addAttribute(new Attribute(a));
					}
				}
			// if b is bigger	
			}else{
				output = new Relation(input.getTupleCount()/right_val);
				
				//V(R',A) = V(R',B) = min( V(R,A), V(R,B) )
				for(Attribute a : input.getAttributes()){
					if(a.equals(left) || a.equals(right)){
						output.addAttribute(new Attribute(a.getName(),left_val));
					}else{
						output.addAttribute(new Attribute(a));
					}
				}
			}
		}
		
		op.setOutput(output);
	}
	
	public void visit(Product op) {
		
		Relation left_input = op.getLeft().getOutput();
		Relation right_input = op.getRight().getOutput();
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
		
		Relation left_input = op.getLeft().getOutput();
		Relation right_input = op.getRight().getOutput();
		Predicate pred = op.getPredicate();
		Attribute left = pred.getLeftAttribute();
		Attribute right = pred.getRightAttribute();
		int left_val = left_input.getAttribute(left).getValueCount();
		int right_val = right_input.getAttribute(right).getValueCount();
		List<Attribute> left_list = left_input.getAttributes();
		List<Attribute> right_list = right_input.getAttributes();
		Relation output;
		
		if(left_val > right_val){
			output = new Relation((left_input.getTupleCount()*right_input.getTupleCount())/left_val);
			for(Attribute a : left_list){
				if(a.equals(left)){
					output.addAttribute(new Attribute(a.getName(),right_val));
				}else{
					output.addAttribute(new Attribute(a));
				}
			}
			for(Attribute b : right_list){
				if(b.equals(right)){
					output.addAttribute(new Attribute(b.getName(),right_val));
				}else{
					output.addAttribute(new Attribute(b));
				}
			}
		}else{
			output = new Relation((left_input.getTupleCount()*right_input.getTupleCount())/right_val);
			for(Attribute a : left_list){
				if(a.equals(left)){
					output.addAttribute(new Attribute(a.getName(),left_val));
				}else{
					output.addAttribute(new Attribute(a));
				}
			}
			for(Attribute b : right_list){
				if(b.equals(right)){
					output.addAttribute(new Attribute(b.getName(),left_val));
				}else{
					output.addAttribute(new Attribute(b));
				}
			}
		}
		
		op.setOutput(output);
	}
}
