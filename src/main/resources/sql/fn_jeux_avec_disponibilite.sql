-- Fonction stockée pour récupérer la liste des jeux avec le nombre d'exemplaires disponibles
-- Exemplaire disponible = louable = true ET loue = false

CREATE FUNCTION fn_jeux_avec_disponibilite()
RETURNS TABLE
AS
RETURN
(
    SELECT
        j.id AS jeu_id,
        j.titre,
        j.tarif_journalier,
        COUNT(CASE WHEN e.louable = 1 AND e.loue = 0 THEN 1 END) AS nombre_exemplaires_disponibles
    FROM jeu j
    LEFT JOIN exemplaire e ON j.id = e.jeu_id
    GROUP BY j.id, j.titre, j.tarif_journalier
);

-- Exemple d'utilisation :
-- SELECT * FROM fn_jeux_avec_disponibilite();
