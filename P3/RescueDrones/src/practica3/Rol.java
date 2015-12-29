package practica3;

public enum Rol {

    MOSCA(0, 2), PAJARO(1, 1), HALCON(2, 4);
    private int id;
    private int consumo;

    private Rol(int idd, int consum) {
        id=idd;
        consumo=consum;
    }

    public static Rol getRol(int id) {
        
        for (Rol col : Rol.values()) {
           if(col.id==id){
               return col;
           }     
        }
        return null;
    }

    public int getConsumo() {
        return this.consumo;
    }

    public int getId() {
	return id;
    }
}
