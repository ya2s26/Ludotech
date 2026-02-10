package fr.eni.ludotech.bll;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(Integer genreId) {
        super("Genre introuvable: " + genreId);
    }
}
