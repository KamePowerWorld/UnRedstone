package quarri6343.unredstone.api;

public class stackSizeInt {
    
    private int value;
    
    public stackSizeInt(int value){
        set(value);
    }
    
    public static boolean isValid(int value){
        return value >= 1 && value <= 64;
    }
    
    public void set(int value){
        if(!isValid(value)){
            throw new IllegalArgumentException();
        }

        this.value = value;
    }
    
    public int get(){
        return value;
    }
}
