package org.deri.tarql.functions;


//package org.deri.tarql;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.ARQ;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.QueryBuildException;
import org.apache.jena.rdf.model.impl.Util;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.ExecutionContext;
import org.apache.jena.sparql.engine.QueryIterator;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.engine.iterator.QueryIterPlainWrapper;
import org.apache.jena.sparql.expr.ExprEvalException;
import org.apache.jena.sparql.function.FunctionRegistry;
import org.apache.jena.sparql.pfunction.PFuncSimpleAndList;
import org.apache.jena.sparql.pfunction.PropFuncArg;
import org.apache.jena.sparql.pfunction.PropertyFunction;
import org.apache.jena.sparql.pfunction.PropertyFunctionFactory;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.apache.jena.sparql.util.IterLib;
import org.apache.jena.sparql.util.Symbol;
import org.deri.tarql.tarql;
import org.deri.tarql.TarqlQuery;


public class Splitfinal implements PropertyFunctionFactory {
int bindno=1;
public static String IRI = tarql.NSS + "split";
public static String NAME2 = "java:split";
public static final Symbol PREFIX_MAPPING2 = Symbol.create("prefixMapping");
static {
	TarqlQuery.registerFunctions();
}
public Splitfinal() {
	super();
}
	@Override
	public PropertyFunction create(String uri) {
		// TODO Auto-generated method stub
		
//		PrefixMapping prefixes = context.get(ExpandPrefixFunction.PREFIX_MAPPING);
//		if (prefixes == null) throw new ExprEvalException("No prefix mapping registered");
		return new PFuncSimpleAndList()
				{
			public QueryIterator execEvaluated(Binding binding, Node subject, Node predicate, PropFuncArg object,
					ExecutionContext execCxt) {
				// TODO Auto-generated method stub
				if (!object.getArg(0).isLiteral() || !object.getArg(1).isLiteral()) {
		            return IterLib.noResults(execCxt);
		        }
		        
		        String s = object.getArg(0).getLiteralLexicalForm() ;
		        String regex = object.getArg(1).getLiteralLexicalForm() ;
		        
		        // StrUtils will also trim whitespace
		        List<String> tokens = Arrays.asList(StrUtils.split(s, regex));
		        HashMap<Integer,String> list=new HashMap<>();
		        for(int i=0;i<tokens.size();i++)
		        {
		        	list.put(bindno,tokens.get(i).toString());
		        	bindno++;
		        }
		        
		        
		        if (Var.isVar(subject)) {
		        	Iterator<Binding> it = null;
		            // Case: Subject is variable. Return all tokens as results.
		            
		            for(Integer hm:list.keySet())
		        	{ String bind= Integer.toString(bindno)+" "+list.get(hm).toString();
		            	final Var subjectVar = Var.alloc(bind);
		            	System.out.println(bind);
		             it = Iter.map(
		                    tokens.iterator(),
		                    item -> BindingFactory.binding(binding, subjectVar,
		                            NodeFactory.createLiteral(item)));
		        	}
		            
		           return new QueryIterPlainWrapper(it, execCxt);
		            
		        } else if ( Util.isSimpleString(subject) ) {
		            // Case: Subject is a plain literal.
		            // Return input unchanged if it is one of the tokens, or nothing otherwise
		            if (tokens.contains(subject.getLiteralLexicalForm())) {
		                return IterLib.result(binding, execCxt);
		            } else {
		                return IterLib.noResults(execCxt);
		            }
		            
		        }
		        //FunctionRegistry.get().put("http://example.org/function#myFunction", Split3.class) ;
//		        public void put(String uri, PropertyFunctionFactory factory) { registry.put(uri,factory) ; }
//		        final PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ARQ.getContext());
//		        reg.put("urn:ex:fn#split", new Split3());
//		        PropertyFunctionRegistry.set(ARQ.getContext(), reg);
		        final Dataset ds = DatasetFactory.createMem();
		        final PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ds.getContext());
		        reg.put("java:split", new Splitfinal());;
		        PropertyFunctionRegistry.set(ds.getContext(), reg);
		        return IterLib.noResults(execCxt);
				}
	};
	
//	public void put(String uri, PropertyFunctionFactory factory)
//    { registry.put(uri,factory) ; 
//    }

}
}
