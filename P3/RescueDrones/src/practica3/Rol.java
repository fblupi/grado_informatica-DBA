package practica3;

public enum Rol {

    MOSCA(0, 2), PAJARO(1, 1), HALCON(2, 4);
    private int id;
    private int consumo;

    private Rol(int id, int consumo) {
	throw new UnsupportedOperationException();
    }

    public static Rol getRol(int id) {
	throw new UnsupportedOperationException();
    }

    public int getConsumo() {
	return this.consumo;
    }
}
