# Backtracking in a multigraph
Proyecto 2 CI2693


# Objetivo
Programa que, dado un mapa, líneas operativas y los nombres de dos paradas, indique las líneas que se deben tomar, y dónde realizar la transferencia (si aplica) entre ellas para conseguir el trayecto con el menor numero de trasbordos. 

# Algoritmo
Esta funcion recorre el grafo como lo recorreria el algoritmo DFS, primero busca todos los sucesores con la misma linea de metro, y ve por esa rama recursivamente, lleva una variable llamada current, si current es mayor o igual al minimo actual, entonces corta la recursion pues buscamos el menor numero de trasbordos, si se llega a un punto donde no hay mas sucesores no visitados de la misma linea entonces busca por las otras lineas, y se le suma 1 a current. Si se llega a pFin entonce se actualiza el minimo.

# Estructura del grafo
Para la estructura interna en la que se guardó el grafo se uso la estructura: 
HashMap<sup>1</sup><Integer,Pair<Vertice,HashMap<sup>2</sup><Vertice,HashMap<sup>3</sup><Integer,Arco»».

Donde en el primer HashMap<sup>1</sup> , las clave son enteros, esto porque el acceso a los vértices se hacía mediante su id, como el id era un entero, entonces era más rapido saber si un vertice existá o no con esta estructura, que, por ejemplo, con Lists.

Lo que contiene este HashMap<sup>1</sup> es un par, donde el primer elemento del par es el Vértice principal, y el segundo valor del par, es otro HashMap<sup>2</sup>.

Dicho HashMap<sup>2</sup>, contiene los vértices con los que el vértice principal está relacionado, este HashMap<sup>2</sup> está indexado por objetos vértices, es decir, esas son las claves, y el contenido de él, es otro HashMap<sup>3</sup>.

Este, a su vez está indexado por enteros, y el contenido son aristas o arcos, dependiendo si el grafo es dirigido o no dirigido. Esto nos permite tener varias aristas o arcos de diferente tipo, aquí el entero del HashMap<sup>3</sup> (que lo indexa) representa el tipo del arco o arista.

Otro detalle de implementación es que para saber cúantos vértices o lados tiene el grafo, usamos un contador que va aumentando cuando se agrega un vértices o lado, y disminuyendo cuando se elimina alguno, de esta manera no tenemos que complicarnos recorriendo la estructura. Esto, no sólo nos hace más fácil la implentación, sino que nos ahorra bastante tiempo de ejecución pues la complejidad de esta operaciń pasa a ser es O(1).

Nota: Se usaron superíndices para representar los HashMap<sup> </sup>para facilidad del lector
