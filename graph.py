import pandas as pd
import matplotlib.pyplot as plt

def plot_graph(file_name, x_label, y_label, title, output_file, columns, labels):
    # Lire le fichier CSV
    df = pd.read_csv(file_name)

    # Créer des graphiques
    plt.figure(figsize=(10, 5))
    for column, label in zip(columns, labels):
        plt.plot(df['n'], df[column], label=label, marker='o')
    plt.xlabel(x_label)
    plt.ylabel(y_label)
    plt.title(title)
    plt.legend()
    plt.grid(True)
    plt.savefig(output_file)
    plt.show()

# Graphiques pour les temps d'insertion en cas moyen
plot_graph(
    'average_case_insertion.csv',
    'Taille de l\'arbre (n)',
    'Temps d\'insertion (ns)',
    'Temps d\'insertion en fonction de la taille de l\'arbre (cas moyen)',
    'average_case_insertion.png',
    ['Average Case Insertion ARN (ns)', 'Average Case Insertion ABR (ns)'],
    ['Average Case Insertion ARN', 'Average Case Insertion ABR']
)

# Graphiques pour les temps de recherche en cas moyen
plot_graph(
    'average_case_search.csv',
    'Taille de l\'arbre (n)',
    'Temps de recherche (ns)',
    'Temps de recherche en fonction de la taille de l\'arbre (cas moyen)',
    'average_case_search.png',
    ['Average Case Search ARN (ns)', 'Average Case Search ABR (ns)'],
    ['Average Case Search ARN', 'Average Case Search ABR']
)

# Graphiques pour les temps d'insertion en pire cas
plot_graph(
    'worst_case_insertion.csv',
    'Taille de l\'arbre (n)',
    'Temps d\'insertion (ns)',
    'Temps d\'insertion en fonction de la taille de l\'arbre (pire cas)',
    'worst_case_insertion.png',
    ['Worst Case Insertion ARN (ns)', 'Worst Case Insertion ABR (ns)'],
    ['Worst Case Insertion ARN', 'Worst Case Insertion ABR']
)

# Graphiques pour les temps de recherche en pire cas
plot_graph(
    'worst_case_search.csv',
    'Taille de l\'arbre (n)',
    'Temps de recherche (ns)',
    'Temps de recherche en fonction de la taille de l\'arbre (pire cas)',
    'worst_case_search.png',
    ['Worst Case Search ARN (ns)', 'Worst Case Search ABR (ns)'],
    ['Worst Case Search ARN', 'Worst Case Search ABR']
)



