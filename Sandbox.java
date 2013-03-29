import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Sandbox {

	Integer id;
	String value;
	Float max;
	List<Integer> li;
	List<String> ls;
	List<Float> lf;
	
	public static void main(String arg[]) throws Exception {
		
		Map<String, String> data = new HashMap<String,String>();
		data.put("id","123");
		data.put("value","abc");
		data.put("max","123.4");
		data.put("li","123,124");

		data.put("ls","123, 124 , qs");
		data.put("lf","123, 124 , qs");

		data.put("s","123,124");
		
		Object object = Sandbox.parse(Sandbox.class, data);
		
		System.out.println(object);
		
		
	}
	
	public static Object parse(Class<?> objectClass, Map<String,String> data) throws Exception{
		
		// build our empty object.
		Object object = objectClass.newInstance();
		
		//loop over values and map them into our object.
		for(String key : data.keySet()){
			try{	
				Field field = object.getClass().getDeclaredField(key);
				String value = data.get(key);
				Class<?> clazz = field.getType();
				
				//basic types
				if(clazz == Integer.class){
					field.set(object, new Integer(value));
				}else if(clazz == Float.class){
					field.set(object, new Float(value));
				}else if(clazz == String.class){
					field.set(object,value);
					
				//List
				}else if(clazz == List.class){
					Type type = field.getGenericType();
					if(type instanceof ParameterizedType){
					    ParameterizedType aType = (ParameterizedType) type;
					    Type[] fieldArgTypes = aType.getActualTypeArguments();
					    for(Type fieldArgType : fieldArgTypes){
					        Class<?> fieldArgClass = (Class<?>) fieldArgType;
					        
					        //Integer
					        if(fieldArgClass == Integer.class){
								List<Integer> list = new ArrayList<Integer>();
								for(String item : value.split("[^0-9]+")){
									list.add(new Integer(item));
								}
								field.set(object, list);
							
							//Float
							}else if(fieldArgClass == Float.class){
								List<Float> list = new ArrayList<Float>();
								for(String item : value.split("[^0-9\\.]+")){
									list.add(new Float(item));
								}
								field.set(object, list);
							
							//String
							}else if(fieldArgClass == String.class){
								List<String> list = new ArrayList<String>();
								for(String item : value.split("\\s*,\\s*")){
									list.add(item);
								}
								field.set(object, list);
							}
					    }
					}
				}
			}catch(NoSuchFieldException e){	
				System.out.println("Unknown field '"+key+"' in class '"+object.getClass().getName()+"' (value:'"+data.get(key)+"')");
			}
		}
		return(object);
	}
	
}
