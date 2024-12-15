import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * <p>
 * Implantation de l'interface Collection basée sur les arbres binaires de
 * recherche. Les éléments sont ordonnés soit en utilisant l'ordre naturel (cf
 * Comparable) soit avec un Comparator fourni à la création.
 * </p>
 * 
 * <p>
 * Certaines méthodes de AbstractCollection doivent être surchargées pour plus
 * d'efficacité.
 * </p>
 * 
 * @param <E>
 *            le type des clés stockées dans l'arbre
 */
public class ABR<E> extends AbstractCollection<E> {
    private Noeud racine;
    private int taille;
    private Comparator<? super E> cmp;

    private class Noeud {
        E cle;
        Noeud gauche;
        Noeud droit;
        Noeud pere;

        Noeud(E cle) {
            this.cle = cle;
            this.droit = null;
            this.gauche = null;
            this.pere = null;
        }

        /**
         * Renvoie le noeud contenant la clé minimale du sous-arbre enraciné
         * dans ce noeud
         * 
         * @return le noeud contenant la clé minimale du sous-arbre enraciné
         *         dans ce noeud
         */
        Noeud minimum() {
            Noeud x = this;
            while (x.gauche != null) {
                x = x.gauche;
            }
            return x;
        }

        /**
         * Renvoie le successeur de ce noeud
         * 
         * @return le noeud contenant la clé qui suit la clé de ce noeud dans
         *         l'ordre des clés, null si c'es le noeud contenant la plus
         *         grande clé
         */
        Noeud suivant() {
            if (droit != null) {
                return droit.minimum();
            }
            Noeud x = this;
            Noeud y = pere;
            while (y != null && x == y.droit) {
                x = y;
                y = y.pere;
            }
            return y;
        }
    }

    // Consructeurs

    /**
     * Crée un arbre vide. Les éléments sont ordonnés selon l'ordre naturel
     */
    public ABR() {
        racine = null;
        taille = 0;
        cmp = (e1, e2) -> ((Comparable<E>) e1).compareTo(e2);
    }

    /**
     * Crée un arbre vide. Les éléments sont comparés selon l'ordre imposé par
     * le comparateur
     * 
     * @param cmp
     *            le comparateur utilisé pour définir l'ordre des éléments
     */
    public ABR(Comparator<? super E> cmp) {
        this();
        this.cmp = cmp;
    }

    /**
     * Constructeur par recopie. Crée un arbre qui contient les mêmes éléments
     * que c. L'ordre des éléments est l'ordre naturel.
     * 
     * @param c
     *            la collection à copier
     */
    public ABR(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public Iterator<E> iterator() {
        return new ABRIterator();
    }

    @Override
    public int size() {
        return taille;
    }

    // Quelques méthodes utiles

    /**
     * Recherche une clé. Cette méthode peut être utilisée par
     * {@link #contains(Object)} et {@link #remove(Object)}
     * 
     * @param o
     *            la clé à chercher
     * @return le noeud qui contient la clé ou null si la clé n'est pas trouvée.
     */
    @SuppressWarnings("unchecked")
    Noeud rechercher(Object o) {
        E k = (E) o;
        Noeud x = racine;
        while (x != null && !x.cle.equals(k)) {
            x = cmp.compare(k, x.cle) < 0 ? x.gauche : x.droit;
        }
        return x;
    }

    @Override
    public boolean add(E e) {
        Noeud z = new Noeud(e);
        Noeud y = null;
        Noeud x = racine;
        while (x != null) {
            y = x;
            x = cmp.compare(z.cle, x.cle) < 0 ? x.gauche : x.droit;
        }
        z.pere = y;
        if (y == null) {
            racine = z;
        } else {
            if (cmp.compare(z.cle, y.cle) < 0)
                y.gauche = z;
            else
                y.droit = z;
        }
        z.gauche = z.droit = null;
        taille++;
        return true;
    }

    /**
     * Supprime le noeud z. Cette méthode peut être utilisée dans
     * {@link #remove(Object)} et {@link Iterator#remove()}
     * 
     * @param z
     *            le noeud à supprimer
     * @return le noeud contenant la clé qui suit celle de z dans l'ordre des
     *         clés. Cette valeur de retour peut être utile dans
     *         {@link Iterator#remove()}
     */
    private Noeud supprimer(Noeud z) {
        Noeud x, y, retour;
        retour = z.suivant();

        if (z.gauche == null || z.droit == null)
            y = z;
        else
            y = z.suivant();

        if (y.gauche != null)
            x = y.gauche;
        else
            x = y.droit;

        if (x != null) x.pere = y.pere;

        if (y.pere == null) {
            racine = x;
        } else {
            if (y == y.pere.gauche)
                y.pere.gauche = x;
            else
                y.pere.droit = x;
        }

        if (y != z) z.cle = y.cle;
        y.pere = null;
        y.droit = null;
        y.gauche = null;

        return retour;
    }

    /**
     * Les itérateurs doivent parcourir les éléments dans l'ordre ! Ceci peut se
     * faire facilement en utilisant {@link Noeud#minimum()} et
     * {@link Noeud#suivant()}
     */
    private class ABRIterator implements Iterator<E> {
        private Noeud next = (racine != null) ? racine.minimum() : null;
        private Noeud prev = null;

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public E next() {
            if (next == null) throw new NoSuchElementException();
            prev = next;
            next = next.suivant();
            return prev.cle;
        }

        @Override
        public void remove() {
            if (prev == null) throw new IllegalStateException();
            next = supprimer(prev);
            prev = null;
            taille--;
        }
    }

    // Pour un "joli" affichage

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        toString(racine, buf, " ", maxStrLen(racine));
        return buf.toString();
    }

    private void toString(Noeud x, StringBuffer buf, String path, int len) {
        if (x == null)
            return;
        toString(x.droit, buf, path + "D", len);
        for (int i = 0; i < path.length(); i++) {
            for (int j = 0; j < len + 6; j++)
                buf.append(' ');
            char c = ' ';
            if (i == path.length() - 1)
                c = '+';
            else if (path.charAt(i) != path.charAt(i + 1))
                c = '|';
            buf.append(c);
        }
        buf.append("-- " + x.cle.toString());
        if (x.gauche != null || x.droit != null) {
            buf.append(" --");
            for (int j = x.cle.toString().length(); j < len; j++)
                buf.append('-');
            buf.append('|');
        }
        buf.append("\n");
        toString(x.gauche, buf, path + "G", len);
    }

    private int maxStrLen(Noeud x) {
        return x == null ? 0 : Math.max(x.cle.toString().length(),
                Math.max(maxStrLen(x.gauche), maxStrLen(x.droit)));
    }

    public static void main(String[] args) {
        ABR<Integer> arbre = new ABR<>();
        Random rand = new Random();

        int[] values = new int[15];
        for (int i = 0; i < values.length; i++) {
            values[i] = rand.nextInt(100);
        }

        // Print all values before adding them to the tree
        System.out.println("La liste des éléments de mon arbre sont :");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (int value : values) {
            System.out.print(value + " ");
        }
        System.out.println();

        for (int valeur : values) {
            arbre.add(valeur);
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.print(" \n Structure de l arbre avant suppression/\n" + arbre);

        for (int value : values) {
            if (arbre.rechercher(value) != null) {
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("Valeur " + value + " trouvée dans l'arbre.");
            } else {
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("Valeur " + value + " non trouvée dans l'arbre.");
            }
        }

        ABR<Integer>.Noeud nodeToDelete1 = arbre.rechercher(values[2]);
        arbre.supprimer(nodeToDelete1);

        ABR<Integer>.Noeud nodeToDelete2 = arbre.rechercher(values[6]);
        arbre.supprimer(nodeToDelete2);

        System.out.println("\n");
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("le size de l arbre " + arbre.size());
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.print(" \n Structure de l arbre apres suppression/\n" + arbre);
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("les valeur supprimer sont : " + values[2] + " " + values[6]);

        System.out.println("\nTest de l'arbre:");
        for (int value : values) {
            if (arbre.rechercher(value) != null) {
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("Valeur " + value + " trouvée dans l'arbre.");
            } else {
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("Valeur " + value + " non trouvée dans l'arbre.");
            }
        }

        // Test de l'itérateur
        System.out.println("\nTest de l'itérateur:");
        Iterator<Integer> iterator = arbre.iterator();
        while (iterator.hasNext()) {
            Integer value = iterator.next();
            System.out.println("Valeur suivante : " + value);
        }
    }
}