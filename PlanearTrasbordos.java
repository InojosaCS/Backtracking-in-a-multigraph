import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*; 

/**
 * @author Cristian Inojosa
 *
 */

public class PlanearTrasbordos {
	
	public static String[] lineasDeMetro;
	public static int nroLineas;
	public static int minimo = Integer.MAX_VALUE;
	public static int nroEstaciones;
	public static String caminoCompleto = "";
	public static HashMap<Integer, Integer> hashing = new HashMap <Integer, Integer>();
	public static Vertice verticeInicio;
	public static Vertice verticeFin;
    /**
     * Llama a las otras funciones para cargar eLa Graformacion y hacer el backtracking
     * @param args Archivo con la nformacion que el usuario proporciona
     */
	public static void main(String[] args) {
	    if(args.length != 4){
            System.err.println("Uso: java PlanearTrasbordos <mapa> <lineas> <pIni> <pFin>");
            return;
        }
        try{
        	BufferedReader Lector = new BufferedReader(new FileReader(args[0]));
			Grafo grafo;
			
			String orientacion = Lector.readLine();
			if(orientacion.contentEquals("N") || orientacion.contentEquals("n")) {
				grafo = new GrafoNoDirigido();
			} else if(orientacion.contentEquals("D") || orientacion.contentEquals("d")) {
				grafo = new GrafoDirigido();
			} else {
				System.out.println("La primera linea del archivo debe contener N o D");
				Lector.close();
				return;
			}
			Lector.close(); 
			
			
			MapeoEstaciones(args[0], args[1]);
			grafo.cargarGrafo(args[0]);
			CambiarTexto b = new CambiarTexto();
			b.RecuperarArchivo(args[0], lineasDeMetro);

			// Busca los vertices iniciales y finales
			Collection<Vertice> vertices = grafo.vertices();
			nroEstaciones = vertices.size();
			
			verticeInicio = new Vertice (-1, "", -1, -1, -1);
			verticeFin = new Vertice (-1, "", -1, -1, -1);
			
			int i=0;
			for(Vertice vertice: vertices) {
				hashing.put(vertice.getId(), i);
				i++;
				if(vertice.getNombre().contentEquals(args[2])) 
					verticeInicio = grafo.obtenerVertice(vertice.getId());
				if(vertice.getNombre().contentEquals(args[3])) 	
					verticeFin = grafo.obtenerVertice(vertice.getId());
			}
			
			if(verticeInicio.getNombre().contentEquals("") || verticeFin.getNombre().contentEquals("")) {
				System.out.println("<pIncio> o <pFin> no pertence al conjunto de estaciones en <mapa>, chequee que esten escritas igual");
				return;
			}
			
			System.out.println(nroEstaciones);
			System.out.println("El programa termina cuando aparezca el mensaje de finalizado, por favor espere");
			if(grafo instanceof GrafoNoDirigido)
				backtracking((GrafoNoDirigido)grafo, verticeInicio, verticeFin);
			else
				backtracking((GrafoDirigido)grafo, verticeInicio, verticeFin);
			
			System.out.println("Finalizado");
        
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato del archivo");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}


	
	private static boolean MapeoEstaciones(String archivo, String archivo1) {
		try {
			// Leer las lineasDeMetro y contar cuantas lineas de metro hay
	        BufferedReader Lector = new BufferedReader(new FileReader(archivo1));
	        nroLineas = 0;
	        
	        while (true) {
	            String linea = Lector.readLine();
	            if (linea == null) {
	                break;
	            }
	            nroLineas++;
	        }
	        Lector.close();
	        
	        // Cambiar las lineasDeMetro
			BufferedReader Lector1 = new BufferedReader(new FileReader(archivo1));
			lineasDeMetro = new String[nroLineas];
			CambiarTexto f = new CambiarTexto();
			
			for(int i = 0; i < nroLineas; i++) {
				String[] helper = Lector1.readLine().split("\\s+");
				lineasDeMetro[i] = helper[0];
				String x = helper[0];
				f.modifyFile(archivo, "\\b"+helper[0]+"\\b", Integer.toString(i));
			}
			Lector1.close();		
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private static String backtracking(GrafoNoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> adyacentes = grafo.adyacentes(inicio.getId());
		int current=0;
		String caminoActual = inicio.getNombre();
		
		// Se crea la tabla de visitados
		int k = 0;
		int [][] tablaVisitados = new int[nroLineas+2][nroEstaciones];
		
		for(Vertice vertice: grafo.vertices()) {
			tablaVisitados [0][k] = vertice.getId();
			for(int j=1; j<nroLineas+1; j++) {
				tablaVisitados[j][k] = 0;
			}
			k++;
		}
		// Se itera sobre sobre los vecinos del nodo inicial
		for(Vertice verticeAdj: adyacentes) {
			System.out.println(verticeAdj.getNombre());
			// Se busca cuales lines existen entre los nodos y se busca un camino entre ellos
			for(int i=0; i<nroLineas; i++) {
				int indiceValido = visitaValida((GrafoNoDirigido) grafo, verticeAdj, i, tablaVisitados);
				if (grafo.estaArista(inicio, verticeAdj, i) && indiceValido!=-1) {
					Arista arista = new Arista(inicio, verticeAdj, i, 0);
					arista = grafo.obtenerArista(inicio, verticeAdj, i);
					BTRecursivo((GrafoNoDirigido) grafo, arista, current, fin.getNombre(), caminoActual, tablaVisitados);
					tablaVisitados[i+1][indiceValido] += 1;
				}
			}
		}
		return caminoCompleto;
	}
	
	static void BTRecursivo(GrafoNoDirigido grafo, Arista arista, int current, String destino, String caminoActual, int [][] tablaVisitados) { 
		// Declaracion de variables
		Vertice inicio = arista.getExtremo1();
		Vertice actual = arista.getExtremo2();
		int tipo = arista.getTipo();
		Set<Vertice> adyacentes = grafo.adyacentes(actual.getId());
		System.out.println(inicio.getNombre()+"->"+actual.getNombre()+" "+tipo);
		
		caminoActual += " " + lineasDeMetro[tipo] + " -> " + actual.getNombre() + " " + Integer.toString(actual.getId());
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getNombre().contentEquals(destino) ) {
			minimo = current < minimo ? current : minimo;
			caminoCompleto = caminoActual;
			System.out.println(caminoCompleto);
			System.out.println(minimo);
			return;
		}
		
		// Si llega a un nodo sin vecinos entonces acaba el backtracking en esa rama
		for(Vertice verticeAdj: adyacentes) {
			// Se chequea si exite una arista del mismo tipo de linea y si 
			// es valido visitarla (esto es, que no haya sido visitado ese nodo por la misma linea)
			int indiceValido = visitaValida((GrafoNoDirigido) grafo, verticeAdj, tipo, tablaVisitados);
			if (grafo.estaArista(actual, verticeAdj, tipo) && indiceValido!=-1 && verticeAdj.getNombre() != inicio.getNombre()) {
				tablaVisitados[tipo+1][indiceValido] += 1;
				Arista aristaAux = new Arista(actual, verticeAdj, tipo, 0);
				aristaAux = grafo.obtenerArista(actual, verticeAdj, tipo);
				BTRecursivo((GrafoNoDirigido) grafo, aristaAux, current, destino, caminoActual, tablaVisitados);
				//break;
				}
		}
		
		for(Vertice verticeAdj: adyacentes) {
			// Se itera sobre todos los adyacentes del nodo actual 
			for(int i=0; i<nroLineas; i++) {
				// Se chequea si exite una arista por cualquier tipo de linea y si 
				// es valido visitarla (esto es, que no haya sido visitado ese nodo por la misma linea)
				int indiceValido = visitaValida((GrafoNoDirigido) grafo, verticeAdj, i, tablaVisitados);
				if (grafo.estaArista(actual, verticeAdj, i) && current + 1 < minimo && indiceValido!=-1 && verticeAdj.getNombre() != inicio.getNombre()) {
					tablaVisitados[i+1][indiceValido] += 1;
					Arista aristaAux = new Arista(actual, verticeAdj, i, 0);
					aristaAux = grafo.obtenerArista(actual, verticeAdj, i);
					BTRecursivo((GrafoNoDirigido) grafo, aristaAux, current+1, destino, caminoActual, tablaVisitados);
				}
			}
		}
		return;
	}
		
	static int visitaValida(GrafoNoDirigido grafo, Vertice v, int tipo, int [][] tablaVisitados) {
		int i=0;
		int ans = -1;
		boolean helper = false;
		// Cheque si una vertice ya ha sido visitado por una linea
		for(Vertice vertice: grafo.vertices()) {
			if(vertice.getId() == v.getId()) {
				helper = tablaVisitados[tipo+1][i]<3;
				break;
			}
			i++;
		}
		return ans = helper ? i : ans;
	}

	private static String backtracking(GrafoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> sucesores = grafo.sucesores(inicio.getId());
		int current=0;
		String caminoActual = inicio.getNombre();

		// Se itera sobre sobre los vecinos del nodo inicial
		for(Vertice verticeAdj: sucesores) {
			System.out.println(verticeAdj.getNombre());
			// Se busca cuales lines existen entre los nodos y se busca un camino entre ellos
			for(int i=0; i<nroLineas; i++) {
				if (grafo.estaArco(inicio, verticeAdj, i)) {
					Arco arco = new Arco(inicio, verticeAdj, i, 0);
					arco = grafo.obtenerArco(inicio, verticeAdj, i);
					boolean [] visitados = new boolean [nroEstaciones];
					visitados[hashing.get(verticeAdj.getId())] = true;
					BTRecursivo((GrafoDirigido) grafo, arco, current, caminoActual, visitados);
				}
			}
		}
		return caminoCompleto;
	}
	
	static void BTRecursivo(GrafoDirigido grafo, Arco arco, int current, String caminoActual, boolean [] visitados) { 
		// Declaracion de variables
		Vertice inicio = arco.getExtremoInicial();
		Vertice actual = arco.getExtremoFinal();
		int tipo = arco.getTipo();
		Set<Vertice> sucesores = grafo.sucesores(actual.getId());
		//System.out.println(inicio.getNombre()+"->"+actual.getNombre()+" "+lineasDeMetro[tipo]);
		
		caminoActual += " " + lineasDeMetro[tipo] + " -> " + actual.getNombre() + " " + Integer.toString(actual.getId());
		if(actual.getId()==verticeInicio.getId()) current = 0;
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getId()==verticeFin.getId() && current < minimo) {
			minimo = current;
			caminoCompleto = caminoActual;
			System.out.println(caminoCompleto);
			System.out.println(minimo);
			return;
		}
		
		// Si llega a un nodo sin vecinos entonces acaba el backtracking en esa rama
		for(Vertice verticeAdj: sucesores) {
			// Se chequea si exite una arco del mismo tipo de linea y si 
			// es valido visitarla (esto es, que no haya sido visitado ese nodo por la misma linea)
			if (grafo.estaArco(actual, verticeAdj, tipo) && verticeAdj.getId() != inicio.getId() && !visitados[hashing.get(verticeAdj.getId())]) {
				visitados[hashing.get(verticeAdj.getId())] = true;
				Arco arcoAux = new Arco(actual, verticeAdj, tipo, 0);
				arcoAux = grafo.obtenerArco(actual, verticeAdj, tipo);
				BTRecursivo((GrafoDirigido) grafo, arcoAux, current, caminoActual, visitados);
				visitados[hashing.get(verticeAdj.getId())] = false;
				//break;
				}
		}
		
		for(Vertice verticeAdj: sucesores) {
			// Se itera sobre todos los sucesores del nodo actual 
			for(int i=0; i<nroLineas; i++) {
				// Se chequea si exite una arco por cualquier tipo de linea y si 
				// es valido visitarla (esto es, que no haya sido visitado ese nodo por la misma linea)
				
				if (grafo.estaArco(actual, verticeAdj, i) && current + 1 < minimo  && !visitados[hashing.get(verticeAdj.getId())]) {
					visitados[hashing.get(verticeAdj.getId())] = true;
					Arco arcoAux = new Arco(actual, verticeAdj, i, 0);
					arcoAux = grafo.obtenerArco(actual, verticeAdj, i);
					current = i==tipo ? current : current+1;
					BTRecursivo((GrafoDirigido) grafo, arcoAux, current, caminoActual, visitados);
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		return;
	}
}
