import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Utils {

	
	public static float variance(Float[] array){ 
		float sum=0; 
		float avg=average(array); 
		for(int i=0;i<array.length;i++){ 
			sum += (array[i]-avg)*(array[i]-avg); 
		} 
		return(sum / array.length); 
	}
	
	
	public static float variance(float[] array, int start, int length){ 
		float sum=0; 
		float avg=average(array,start,length); 
		for(int	i=0;i<length;i++){
			int idx = start - i;
			if(idx<0){
				idx += array.length;
			} 
			sum += (array[i]-avg)*(array[i]-avg); 
		} 
		return(sum / length); 
	}

	
	public static float average(Float[] array) {
		float sum = 0;
		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return (sum / array.length);
	}
	
	
	public static float average(float[] array, int start, int length){ 
		float sum=0; 
		for(int	i=0;i<length;i++){
			int idx = start - i;
			if(idx>=0){
				sum += array[idx];
			}else{
				sum += array[idx+array.length];
			}
			 
		}
		return(sum/length);
	}

	public static float averageV(float[] array, int start, int length){ 
		float sum=0; 
		for(int	i=0;i<length;i++){
			int idx = start - i;
			if(idx>=0){
				sum += array[idx]*(length-i);
			}else{
				sum += array[idx+array.length]*(length-i);
			}
			 
		}
		return(sum*2/(length*(length+1)) );
	}
	
	/*public static float average(float[] array, int start, int length,float threshold){ 
		float sum=0; 
		int cpt=0;
		for(int	i=0;i<length;i++){
			int idx = start - i;
			if(idx<0){
				idx += array.length;
			}
			if(array[idx]>threshold){
				sum += array[idx]; 
				cpt++;
			}
		}
		if(cpt>0)
			return(sum/cpt);
		else
			return(0);
	}*/
	
	public static float max(float[] array) {
		float max = Float.MIN_VALUE;
		for (int i = 0; i < array.length; i++) {
			max = (array[i] > max) ? array[i] : max;
		}
		return (max);
	}
	
	
	public static float max(float[] array, int start, int length){ 
		float max=Float.MIN_VALUE; 
		for(int	i=0;i<length;i++){
			int idx = start - i;
			if(idx<0){
				idx += array.length;
			}
			max = (array[idx]>max)?array[idx]:max; 
		}
		return(max);
	}
	
	
	public static float min(float[] array){ 
		float min=Float.MAX_VALUE; 
		for(int	i=0;i<array.length;i++){
			min = (array[i]<min)?array[i]:min; 
		}
		return(min);
	}
	
	
	public static float min(float[] array, int start, int length){ 
		float min=Float.MAX_VALUE; 
		for(int	i=0;i<length;i++){
			int idx = start - i;
			if(idx<0){
				idx += array.length;
			}
			min = (array[idx]<min)?array[idx]:min; 
		}
		return(min);
	}
	
	
	public static float[] sum(float[] array, float[] array2) {
		float[] array3 = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			array3[i] += array[i] + array2[i];
		}
		return (array3);
	}
	
	
	public static float ScaleIEC(float db) {
		float pct = 0.0f;
		if (db < -70.0)
			pct = 0.0f;
		else if (db < -60.0)
			pct = (db + 70.0f) * 0.25f;
		else if (db < -50.0)
			pct = (db + 60.0f) * 0.5f + 2.5f;
		else if (db < -40.0)
			pct = (db + 50.0f) * 0.75f + 7.5f;
		else if (db < -30.0)
			pct = (db + 40.0f) * 1.5f + 15.0f;
		else if (db < -20.0)
			pct = (db + 30.0f) * 2.0f + 30.0f;
		else if (db < 0.0)
			pct = (db + 20.0f) * 2.5f + 50.0f;
		else
			pct = 100.0f;
		return pct;
	}
	
	public static List<Field> getAllFields(Class<?> type) {
		return(getAllFields(new ArrayList<Field>(),type));
	}
	
	public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
	    for (Field field: type.getDeclaredFields()) {
	        fields.add(field);
	    }

	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}

}
