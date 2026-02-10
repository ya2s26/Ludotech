package fr.eni.ludotech.bll;

public class ExemplaireNotFoundException extends RuntimeException {
    public ExemplaireNotFoundException(String codeBarre) {
        super("Exemplaire avec code-barre " + codeBarre + " non trouvé");
    }
}
