/**
*  En este archivo se encuentran los algoritmos de planeamiento de rutas, el de bcaktracking y el de A estrella
*  Explicacion Backtracking:
*  Esta funcion recorre el grafo como lo recorreria el algoritmo DFS, primero busca todos los sucesores con la misma linea de metro ,
*  (Recorre todas lineas pero primero la del mismo tipo para mantener menos trslados, o sea, es una optimizacion
*  y ve por esa rama recursivamente, lleva una variable llamada current, si current es mayor o igual al minimo actual, entonces corta
*  recursion pues buscamos el menor numero de trasbordos, si se llega a un punto donde no hay mas sucesores no visitados de la misma linea 
*  entonces busca por las otras lineas, y se le suma 1 a current. Si se llega a pFin entonce se actualiza el minimo.
*/

import java.util.*; 

/**
 * @author Cristian Inojosa 17-10285
 * @author Alejandro Salazar 16-11080
 */

public class PlanearRutas {

	/**
	 * Variables que guarda los nombres de las lineas de metro
	 */
	private String[] lineasDeMetro;
	
	/**
	 * Variables que guardan el numero de lineas y estaciones
	 */
	private int nroLineas, nroEstaciones;
	
	/**
	 * Variable que representa la cantidad minima de trasbordos para cualquier etapa de la arborescencia 
	 * del recorrido del grafo, sirve para acotar el numero de trasbordos posibles durante el backtracking
	 * Se inicializa en 7 para optimizar el backtracking, ya que en Caracas no hay ninguna ruta que tome mas de 7 
	 * trasbordos
	 */
	public static int minimo;
	
	/**
	 * Variable se va actualizando en cada etapa del recorrido del grafo
	 */
	private String caminoCompleto;
	
	/**
	 * Variable que sirve para mapear N id's de N vertices con una secuencia de {0,2,3...;N-1} 
	 * se usa para marcar los vertices como visitados en O(n) en complejidad de espacio
	 */
	private HashMap<Integer, Integer> hashing = new HashMap <Integer, Integer>();
	
	/**
	 * Variables que marcan los vertices de inicio y fin de camino que estamos buscando
	 */
	private Vertice verticeInicio, verticeFin;
    
	/**
	 * Constructor de la clase
	 */
	PlanearRutas(String[] lineasDeMetro, int nroLineas, int nroEstaciones, int minimo, HashMap<Integer, Integer> hashing, Vertice verticeInicio, Vertice verticeFin){
		this.lineasDeMetro = lineasDeMetro;
		this.nroLineas = nroLineas;
		this.nroEstaciones = nroEstaciones;
		this.minimo = minimo;
		this.hashing = hashing;
		this.verticeInicio = verticeInicio;
		this.verticeFin = verticeFin;
	}
	/**
     * Este funcion busca los sucesores del vertice pInicio, y llama a la funcion BTRecursivo sobre ellos, y marca el vertice inical 
     * como visitado
     * @param GrafoD grafo
     * @param Vertice inicio
     * @param Vertice Fin
     * @return String caminoCompleto : Es el camino con todas las estaciones y lineas
     */
	public String backtracking(GrafoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> sucesores = grafo.sucesores(verticeInicio.getId());
		String caminoActual = inicio.getId() + " "+ inicio.getNombre();
		boolean [] visitados = new boolean [nroEstaciones];
		visitados[hashing.get(verticeInicio.getId())] = true;
		caminoCompleto = "-1";
		
		// Se itera sobre sobre los vecinos del nodo inicial
		for(Vertice verticeAdj: sucesores) {
			// Se busca cuales lines existen entre los nodos y se busca un camino entre ellos
			for(int i=0; i<nroLineas; i++) {
				if (grafo.estaArco(inicio, verticeAdj, i) && !visitados[hashing.get(verticeAdj.getId())]) {
					Arco arco = new Arco(inicio, verticeAdj, i, 0);
					arco = grafo.obtenerArco(inicio, verticeAdj, i);
					visitados[hashing.get(verticeAdj.getId())] = true;
					BTRecursivo((GrafoDirigido) grafo, arco, 0, caminoActual, visitados);
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		return caminoCompleto;
	}
	
	/**
     * Esta funcion recorre el grafo como lo recorreria el algoritmo DFS, primero busca todos los sucesores con la misma linea de metro,
     * y ve por esa rama recursivamente, lleva una variable llamada current, si current es mayor o igual al minimo actual, entonces corta
     * recursion pues buscamos el menor numero de trasbordos, si se llega a un punto donde no hay mas sucesores no visitados de la misma linea 
     * entonces busca por las otras lineas, y se le suma 1 a current. Si se llega a pFin entonce se actualiza el minimo.
     * @param GrafoD grafo
     * @param Arco arco: Pasa el arco entre el nodo actual y su sucesor
     * @param int current: Numero de trasbordos en esa rama
     * @param String CaminoActual: Va guardando el recorrido de la ruta
     * @param visitados: Guarda cuales estaciones del metro ya han sido visitadas
     */
 void BTRecursivo(GrafoDirigido grafo, Arco arco, int current, String caminoActual, boolean [] visitados) { 
		// Declaracion de variables
		Vertice inicio = arco.getExtremoInicial();
		Vertice actual = arco.getExtremoFinal();
		int tipo = arco.getTipo();
		
		// Marco la estacion actual como visitada
		visitados[hashing.get(actual.getId())] = true;
		Set<Vertice> sucesores = grafo.sucesores(actual.getId());
		
		// Se actualiza el recorrido con la estacion y la linea actual
		caminoActual += " " + lineasDeMetro[tipo] + " " + Integer.toString(actual.getId()) + " " + actual.getNombre();
		String[] helper = caminoActual.split("\\s+");
		
		// Algunas condiciones que entorpecen el recorrido
		if(actual.getId()==verticeInicio.getId() || helper.length>200 || minimo==0 ) {	return;}
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getId()==verticeFin.getId() && current < minimo) {
			minimo = current;
			caminoCompleto = minimo + " " + caminoActual;
			//System.out.println(caminoCompleto);
			//System.out.println(minimo);
			return;
		}
		// Se verifica primero si tiene algun adyacente en esa misma linea
		for(Vertice verticeAdj: sucesores) {
			// Se chequea si exite una arco del mismo tipo de linea y si es valido visitarla
			if (grafo.estaArco(actual, verticeAdj, tipo) && verticeAdj.getId() != inicio.getId() && !visitados[hashing.get(verticeAdj.getId())]) {
				visitados[hashing.get(verticeAdj.getId())] = true;
				Arco arcoAux = new Arco(actual, verticeAdj, tipo, 0);
				arcoAux = grafo.obtenerArco(actual, verticeAdj, tipo);
				BTRecursivo((GrafoDirigido) grafo, arcoAux, current, caminoActual, visitados);
				// Se marca como falso para que no estorbe en los otros recorridos
				visitados[hashing.get(verticeAdj.getId())] = false;
				}
		}
		
		for(Vertice verticeAdj: sucesores) {
			// Se itera sobre todos los sucesores del nodo actual 
			for(int i=0; i<nroLineas; i++) {
				// Se chequea si exite una arco por cualquier tipo de linea y si es valido visitarla 
				if (grafo.estaArco(actual, verticeAdj, i) && current + 1 < minimo  && !visitados[hashing.get(verticeAdj.getId())]) {
					visitados[hashing.get(verticeAdj.getId())] = true;
					Arco arcoAux = new Arco(actual, verticeAdj, i, 0);
					arcoAux = grafo.obtenerArco(actual, verticeAdj, i);
					if(current+1<minimo && i!=tipo) {
						BTRecursivo((GrafoDirigido) grafo, arcoAux, current+1, caminoActual, visitados);
						//visitados[hashing.get(masCercanox.getId())] = false;
					}
					// Se marca como falso para que no estore en los otros recorridos
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		// Marco la estacion actual como visitada, por si acaso
		visitados[hashing.get(actual.getId())] = true;
		return;
	}
	/**
     * Esta funcion busca los adyacentes del vertice pInicio, y llama a la funcion BTRecursivo sobre ellos, y marca el vertice inical 
     * como visitado
     * @param GrafoD grafo
     * @param Vertice inicio
     * @param Vertice Fin
     * @return String caminoCompleto : Es el camino con todas las estaciones y lineas
     */
	public String backtracking(GrafoNoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> adyacentes = grafo.adyacentes(verticeInicio.getId());
		String caminoActual = inicio.getId() + " "+ inicio.getNombre();
		boolean [] visitados = new boolean [nroEstaciones];
		visitados[hashing.get(verticeInicio.getId())] = true;
		caminoCompleto = "-1";
		
		// Se itera sobre sobre los vecinos del nodo inicial
		for(Vertice verticeAdj: adyacentes) {
			// Se busca cuales lines existen entre los nodos y se busca un camino entre ellos
			for(int i=0; i<nroLineas; i++) {
				if (grafo.estaArista(inicio, verticeAdj, i) && !visitados[hashing.get(verticeAdj.getId())]) {
					Arista arista = new Arista(inicio, verticeAdj, i, 0);
					arista = grafo.obtenerArista(inicio, verticeAdj, i);
					visitados[hashing.get(verticeAdj.getId())] = true;
					BTRecursivo((GrafoNoDirigido) grafo, inicio, arista, 0, caminoActual, visitados);
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		return caminoCompleto;
	}
	
	/**
     * Esta funcion recorre el grafo como lo recorreria el algoritmo DFS, primero busca todos los sucesores con la misma linea de metro,
     * y ve por esa rama recursivamente, lleva una variable llamada current, si current es mayor o igual al minimo actual, entonces corta
     * recursion pues buscamos el menor numero de trasbordos, si se llega a un punto donde no hay mas sucesores no visitados de la misma linea 
     * entonces busca por las otras lineas, y se le suma 1 a current. Si se llega a pFin entonce se actualiza el minimo.
     * @param GrafoD grafo
     * @param Vertice inicio: Como el TAD no diferencia entre {u,v} y {v,u} pero para el recorrido importa, entonces le pasamos u
     * @param Arco arco: Pasa el arco entre el nodo actual y su sucesor
     * @param int current: Numero de trasbordos en esa rama
     * @param String CaminoActual: Va guardando el recorrido de la ruta
     * @param visitados: Guarda cuales estaciones del metro ya han sido visitadas
    */
 void BTRecursivo(GrafoNoDirigido grafo, Vertice previo, Arista arista, int current, String caminoActual, boolean [] visitados) { 
		//  Como el TAD no diferencia entre {u,v} y {v,u} buscamos cual es cuale
		Vertice inicio = previo.getId() == arista.getExtremo1().getId() ? arista.getExtremo1() : arista.getExtremo2() ;
		Vertice actual = previo.getId() == arista.getExtremo2().getId() ? arista.getExtremo1() : arista.getExtremo2() ;
		int tipo = arista.getTipo();
		
		// Marista la estacion actual como visitada
		visitados[hashing.get(actual.getId())] = true;
		Set<Vertice> adyacentes = grafo.adyacentes(actual.getId());
		
		caminoActual += " " + lineasDeMetro[tipo] + " " + Integer.toString(actual.getId()) + " " + actual.getNombre();
		String[] helper = caminoActual.split("\\s+");
		
		// Algunas condiciones que entorpecen el recorrido
		if(actual.getId()==verticeInicio.getId() || helper.length>200 || minimo==0 ) {	return;}
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getId()==verticeFin.getId() && current < minimo) {
			minimo = current;
			caminoCompleto = minimo + " " + caminoActual;
			//System.out.println(caminoCompleto);
			//System.out.println(minimo);
			return;
		}
		// Se verifica primero si tiene algun adyacente en esa misma linea
		for(Vertice verticeAdj: adyacentes) {
			// Se chequea si exite una arista del mismo tipo de linea y si es valido visitarla
			if (grafo.estaArista(actual, verticeAdj, tipo) && verticeAdj.getId() != inicio.getId() && !visitados[hashing.get(verticeAdj.getId())]) {
				visitados[hashing.get(verticeAdj.getId())] = true;
				Arista aristaAux = new Arista(actual, verticeAdj, tipo, 0);
				aristaAux = grafo.obtenerArista(actual, verticeAdj, tipo);
				BTRecursivo((GrafoNoDirigido) grafo, actual, aristaAux, current, caminoActual, visitados);
				// Se marca como falso para que no estorbe en los otros recorridos
				visitados[hashing.get(verticeAdj.getId())] = false;
				}
		}
		
		for(Vertice verticeAdj: adyacentes) {
			// Se itera sobre todos los adyacentes del nodo actual 
			for(int i=0; i<nroLineas; i++) {
				// Se chequea si exite una arista por cualquier tipo de linea y si es valido visitarla 
				if (grafo.estaArista(actual, verticeAdj, i) && current + 1 < minimo  && !visitados[hashing.get(verticeAdj.getId())]) {
					visitados[hashing.get(verticeAdj.getId())] = true;
					Arista aristaAux = new Arista(actual, verticeAdj, i, 0);
					aristaAux = grafo.obtenerArista(actual, verticeAdj, i);
					if(current+1<minimo && i!=tipo) {
						BTRecursivo((GrafoNoDirigido) grafo, actual, aristaAux, current+1, caminoActual, visitados);
					}
					// Se marca como falso para que no estore en los otros recorridos
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		// Marista la estacion actual como visitada, por si acaso
		visitados[hashing.get(actual.getId())] = true;
		return;
	}

}