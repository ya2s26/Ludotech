package fr.eni.ludotech.dal.projection;

public interface JeuDisponibiliteProjection {
    Integer getJeuId();
    String getTitre();
    Long getNombreExemplairesDisponibles();
}
