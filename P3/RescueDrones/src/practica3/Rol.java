package practica3;

public enum Rol {

    MOSCA(0, 2, 0), PAJARO(1, 1, 1), HALCON(2, 4, 2);
    private int id;
    private int consumo;
    private int prioridad;

    private Rol(int idd, int consum, int prio) {
        id=idd;
        consumo=consum;
	prioridad=prio;
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

    public int getPrioridad() {
	return prioridad;
    }
}
