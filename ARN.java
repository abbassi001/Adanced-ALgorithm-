import java.util.*;

public class ARN<E> extends AbstractCollection<E> {

    // Classe interne représentant un nœud
    public static class Noeud<E> {
        E donnee;
        Noeud<E> gauche;
        Noeud<E> droite;
        Noeud<E> parent;
        int hauteur;
        boolean couleur;

        public Noeud(E donnee) {
            this.donnee = donnee;
        }

        public E getDonnee() {
            return donnee;
        }
    }

    protected Noeud<E> racine;
    static final boolean ROUGE = false;
    static final boolean NOIR = true;

    public Noeud<E> getRacine() {
        return racine;
    }

    // Recherche d'un nœud par sa clé
    public Noeud<E> rechercherNoeud(E cle) {
        Noeud<E> noeud = racine;
        while (noeud != null) {
            if (cle.equals(noeud.donnee)) {
                return noeud;
            } else if (((Comparable<E>)cle).compareTo(noeud.donnee) < 0) {
                noeud = noeud.gauche;
            } else {
                noeud = noeud.droite;
            }
        }
        return null;
    }

    // Méthode pour ajouter un élément à l'arbre
    @Override
    public boolean add(E cle) {
        Noeud<E> noeud = racine;
        Noeud<E> parent = null;

        // Recherche de l'emplacement pour insérer le nouveau nœud
        while (noeud != null) {
            parent = noeud;
            int comparaison = ((Comparable<E>) cle).compareTo(noeud.donnee);
            if (comparaison < 0) {
                noeud = noeud.gauche; // Aller à gauche si la clé est plus petite
            } else if (comparaison > 0) {
                noeud = noeud.droite; // Aller à droite si la clé est plus grande
            } else {
                // Si doublon, insérer dans un sous-arbre gauche distinct
                noeud = noeud.gauche;
            }
        }

        // Création du nouveau nœud
        Noeud<E> nouveauNoeud = new Noeud<>(cle);
        nouveauNoeud.couleur = ROUGE;
        nouveauNoeud.parent = parent;

        // Insertion du nouveau nœud
        if (parent == null) {
            racine = nouveauNoeud; // L'arbre était vide, on initialise la racine
        } else {
            int comparaison = ((Comparable<E>) cle).compareTo(parent.donnee);
            if (comparaison < 0) {
                parent.gauche = nouveauNoeud; // Insertion à gauche si clé plus petite
            } else if (comparaison > 0) {
                parent.droite = nouveauNoeud; // Insertion à droite si clé plus grande
            } else {
                // Si doublon, insérer dans un sous-arbre gauche
                Noeud<E> temp = parent.gauche;
                parent.gauche = nouveauNoeud;
                nouveauNoeud.gauche = temp;
                if (temp != null) {
                    temp.parent = nouveauNoeud;
                }
            }
        }

        // Ajustement des propriétés de l'arbre rouge-noir après insertion
        corrigerProprietesApresInsertion(nouveauNoeud);

        // La racine doit toujours être noire
        racine.couleur = NOIR;
        return true; // L'ajout a été effectué avec succès
    }

    // Correction des propriétés de l'arbre rouge-noir après insertion
    private void corrigerProprietesApresInsertion(Noeud<E> noeud) {
        Noeud<E> parent = noeud.parent;

        if (parent == null) {
            return;
        }

        if (parent.couleur == NOIR) {
            return;
        }

        Noeud<E> grandParent = parent.parent;

        if (grandParent == null) {
            parent.couleur = NOIR;
            return;
        }

        Noeud<E> oncle = getOncle(parent);

        if (oncle != null && oncle.couleur == ROUGE) {
            parent.couleur = NOIR;
            grandParent.couleur = ROUGE;
            oncle.couleur = NOIR;
            corrigerProprietesApresInsertion(grandParent);
        } else if (parent == grandParent.gauche) {
            if (noeud == parent.droite) {
                rotationGauche(parent);
                parent = noeud;
            }
            rotationDroite(grandParent);
            parent.couleur = NOIR;
            grandParent.couleur = ROUGE;
        } else {
            if (noeud == parent.gauche) {
                rotationDroite(parent);
                parent = noeud;
            }
            rotationGauche(grandParent);
            parent.couleur = NOIR;
            grandParent.couleur = ROUGE;
        }
    }

    private Noeud<E> getOncle(Noeud<E> parent) {
        Noeud<E> grandParent = parent.parent;
        if (grandParent.gauche == parent) {
            return grandParent.droite;
        } else if (grandParent.droite == parent) {
            return grandParent.gauche;
        } else {
            throw new IllegalStateException("Le parent n'est pas un enfant de son grand-parent");
        }
    }

    // Méthode pour supprimer un élément de l'arbre
    @Override
    public boolean remove(Object cle) {
        Noeud<E> noeud = racine;

        while (noeud != null && !noeud.donnee.equals(cle)) {
            if (((Comparable<E>)cle).compareTo(noeud.donnee) < 0) {
                noeud = noeud.gauche;
            } else {
                noeud = noeud.droite;
            }
        }

        if (noeud == null) {
            return false; // L'élément n'existe pas
        }

        Noeud<E> noeudDeplace;
        boolean couleurNoeudSupprime;

        if (noeud.gauche == null || noeud.droite == null) {
            noeudDeplace = supprimerNoeudAvecZeroOuUnEnfant(noeud);
            couleurNoeudSupprime = noeud.couleur;
        } else {
            Noeud<E> successeurEnOrdre = trouverMinimum(noeud.droite);
            noeud.donnee = successeurEnOrdre.donnee;
            noeudDeplace = supprimerNoeudAvecZeroOuUnEnfant(successeurEnOrdre);
            couleurNoeudSupprime = successeurEnOrdre.couleur;
        }

        if (couleurNoeudSupprime == NOIR) {
            corrigerProprietesApresSuppression(noeudDeplace);
        }

        return true;
    }

    private Noeud<E> supprimerNoeudAvecZeroOuUnEnfant(Noeud<E> noeud) {
        if (noeud.gauche != null) {
            remplacerEnfantDuParent(noeud.parent, noeud, noeud.gauche);
            return noeud.gauche;
        } else if (noeud.droite != null) {
            remplacerEnfantDuParent(noeud.parent, noeud, noeud.droite);
            return noeud.droite;
        } else {
            Noeud<E> nouvelEnfant = noeud.couleur == NOIR ? new NoeudNul<>() : null;
            remplacerEnfantDuParent(noeud.parent, noeud, nouvelEnfant);
            return nouvelEnfant;
        }
    }

    private Noeud<E> trouverMinimum(Noeud<E> noeud) {
        while (noeud.gauche != null) {
            noeud = noeud.gauche;
        }
        return noeud;
    }

    private void corrigerProprietesApresSuppression(Noeud<E> noeud) {
        if (noeud == racine) {
            return;
        }

        Noeud<E> frere = getFrere(noeud);

        if (frere.couleur == ROUGE) {
            gererFrereRouge(noeud, frere);
            frere = getFrere(noeud);
        }

        if (estNoir(frere.gauche) && estNoir(frere.droite)) {
            frere.couleur = ROUGE;

            if (noeud.parent.couleur == ROUGE) {
                noeud.parent.couleur = NOIR;
            } else {
                corrigerProprietesApresSuppression(noeud.parent);
            }
        } else {
            gererFrereNoirAvecAuMoinsUnEnfantRouge(noeud, frere);
        }
    }

    private void gererFrereRouge(Noeud<E> noeud, Noeud<E> frere) {
        frere.couleur = NOIR;
        noeud.parent.couleur = ROUGE;

        if (noeud == noeud.parent.gauche) {
            rotationGauche(noeud.parent);
        } else {
            rotationDroite(noeud.parent);
        }
    }

    private void gererFrereNoirAvecAuMoinsUnEnfantRouge(Noeud<E> noeud, Noeud<E> frere) {
        boolean noeudEstEnfantGauche = noeud == noeud.parent.gauche;

        if (noeudEstEnfantGauche && estNoir(frere.droite)) {
            frere.gauche.couleur = NOIR;
            frere.couleur = ROUGE;
            rotationDroite(frere);
            frere = noeud.parent.droite;
        } else if (!noeudEstEnfantGauche && estNoir(frere.gauche)) {
            frere.droite.couleur = NOIR;
            frere.couleur = ROUGE;
            rotationGauche(frere);
            frere = noeud.parent.gauche;
        }

        frere.couleur = noeud.parent.couleur;
        noeud.parent.couleur = NOIR;
        if (noeudEstEnfantGauche) {
            frere.droite.couleur = NOIR;
            rotationGauche(noeud.parent);
        } else {
            frere.gauche.couleur = NOIR;
            rotationDroite(noeud.parent);
        }
    }

    private Noeud<E> getFrere(Noeud<E> noeud) {
        Noeud<E> parent = noeud.parent;
        if (noeud == parent.gauche) {
            return parent.droite;
        } else if (noeud == parent.droite) {
            return parent.gauche;
        } else {
            throw new IllegalStateException("Le parent n'est pas un enfant de son grand-parent");
        }
    }

    private boolean estNoir(Noeud<E> noeud) {
        return noeud == null || noeud.couleur == NOIR;
    }

    // Rotation à droite
    private void rotationDroite(Noeud<E> noeud) {
        Noeud<E> parent = noeud.parent;
        Noeud<E> enfantGauche = noeud.gauche;

        noeud.gauche = enfantGauche.droite;
        if (enfantGauche.droite != null) {
            enfantGauche.droite.parent = noeud;
        }

        enfantGauche.droite = noeud;
        noeud.parent = enfantGauche;

        remplacerEnfantDuParent(parent, noeud, enfantGauche);
    }

    // Rotation à gauche
    private void rotationGauche(Noeud<E> noeud) {
        Noeud<E> parent = noeud.parent;
        Noeud<E> enfantDroit = noeud.droite;

        noeud.droite = enfantDroit.gauche;
        if (enfantDroit.gauche != null) {
            enfantDroit.gauche.parent = noeud;
        }

        enfantDroit.gauche = noeud;
        noeud.parent = enfantDroit;

        remplacerEnfantDuParent(parent, noeud, enfantDroit);
    }

    // Remplacer le parent de l'enfant
    private void remplacerEnfantDuParent(Noeud<E> parent, Noeud<E> ancienEnfant, Noeud<E> nouvelEnfant) {
        if (parent == null) {
            racine = nouvelEnfant;
        } else if (parent.gauche == ancienEnfant) {
            parent.gauche = nouvelEnfant;
        } else if (parent.droite == ancienEnfant) {
            parent.droite = nouvelEnfant;
        }

        if (nouvelEnfant != null) {
            nouvelEnfant.parent = parent;
        }
    }

    // Méthode pour réinitialiser l'arbre
    @Override
    public void clear() {
        racine = null;
    }

    // Méthode pour obtenir le nombre d'éléments dans l'arbre
    @Override
    public int size() {
        return getTaille(racine);
    }

    private int getTaille(Noeud<E> noeud) {
        if (noeud == null) {
            return 0;
        }

        return 1 + getTaille(noeud.gauche) + getTaille(noeud.droite);
    }

    // Méthode pour itérer sur les éléments de l'arbre
    @Override
    public Iterator<E> iterator() {
        List<E> elements = new ArrayList<>();
        parcoursInfixe(racine, elements);
        return elements.iterator();
    }

    private void parcoursInfixe(Noeud<E> noeud, List<E> elements) {
        if (noeud != null) {
            parcoursInfixe(noeud.gauche, elements);
            elements.add(noeud.donnee);
            parcoursInfixe(noeud.droite, elements);
        }
    }

    // Recherche d'un élément dans l'arbre
    @Override
    public boolean contains(Object cle) {
        return rechercherNoeud((E) cle) != null;
    }
    
    public class NoeudNul<E> extends Noeud<E> {
        public NoeudNul() {
            super(null);  // Le "null" ici représente l'absence de données
            this.couleur = NOIR; // Un nœud "Nil" est toujours noir
        }

        @Override
        public E getDonnee() {
            throw new UnsupportedOperationException("Un NoeudNul n'a pas de données");
        }

        public Noeud<E> getGauche() {
            return this;  // Un NoeudNul est son propre enfant gauche
        }

        public Noeud<E> getDroite() {
            return this;  // Un NoeudNul est son propre enfant droit
        }

        public Noeud<E> getParent() {
            return null;  // Un NoeudNul n'a pas de parent
        }
    }
    // Méthode pour afficher l'arbre en chaîne de caractères
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(racine, sb, "", 4);
        return sb.toString();
    }

    private void toString(Noeud<E> x, StringBuilder sb, String chemin, int len) {
        if (x == null || x instanceof NoeudNul) return;

        toString(x.droite, sb, chemin + "D", len);

        for (int i = 0; i < chemin.length(); i++) {
            for (int j = 0; j < len; j++) sb.append(' ');
            char c = ' ';
            if (i == chemin.length() - 1)
                c = '+';
            else if (chemin.charAt(i) != chemin.charAt(i + 1))
                c = '|';
            sb.append(c);
        }

        sb.append("-- ").append(x.donnee).append(x.couleur == ROUGE ? "R" : "N").append("\n");

        toString(x.gauche, sb, chemin + "G", len);
    }

    public static void main(String... args) {
        ARN<Integer> arbre = new ARN<>();
    
        // Ajout des éléments à l'arbre
        int[] elementsToAdd = {1, 7, 8, 10, 15, 7, 12, 19, 31, 38};
        for (int element : elementsToAdd) {
            arbre.add(element);
            System.out.println("Après ajout de " + element + " :");
            System.out.println(arbre);
        }
    
        // Tests de recherche
        System.out.println("------------------------------------------------------------------------------------");
        System.out.println("Tests de recherche :");
    
        int[] elementsToSearch = {1, 7, 8, 10, 15, 19, 31, 38};
        for (int element : elementsToSearch) {
            if (arbre.contains(element)) {
                System.out.println("L'élément " + element + " est présent dans l'arbre.");
            } else {
                System.out.println("L'élément " + element + " n'est pas présent dans l'arbre.");
            }
        }
    
        // Suppression d'éléments
        int[] elementsToRemove = {8, 10, 7, 31};
        for (int element : elementsToRemove) {
            arbre.remove(element);
            System.out.println("Après suppression de " + element + " :");
            System.out.println(arbre);
        }
    
        // Tests de recherche après suppression
        System.out.println("--------------------------------------------------------------------------------------");

    }
}