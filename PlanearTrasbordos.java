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
	public static String caminoCompleto;
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
			Grafo grafoInducido;
			
			String orientacion = Lector.readLine();
			if(orientacion.contentEquals("N") || orientacion.contentEquals("n")) {
				grafo = new GrafoNoDirigido();
				grafoInducido = new GrafoNoDirigido();
			} else if(orientacion.contentEquals("D") || orientacion.contentEquals("d")) {
				grafo = new GrafoDirigido();
				grafoInducido = new GrafoDirigido();
			} else {
				System.out.println("La primera linea del archivo debe contener N o D");
				Lector.close();
				return;
			}
			Lector.close(); 
			
			LeerEstaciones(args[0]);
			grafo.cargarGrafo(args[0]);
			grafoInducido = grafo;
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
			if(grafo instanceof GrafoNoDirigido) {
				backtracking((GrafoNoDirigido)grafo, verticeInicio, verticeFin);
				InducirGrafo(args[1], (GrafoNoDirigido) grafoInducido);
        	}else {
				backtracking((GrafoDirigido)grafo, verticeInicio, verticeFin);
				InducirGrafo(args[1], (GrafoDirigido) grafoInducido);
        	}
			System.out.println("#### Finalizado ####");
			String ans;
			minimo = Integer.MAX_VALUE;

			if(grafoInducido instanceof GrafoNoDirigido) 
				ans = backtracking((GrafoNoDirigido)grafo, verticeInicio, verticeFin);
			else
				ans = backtracking((GrafoDirigido)grafoInducido, verticeInicio, verticeFin);
			
			System.out.println(ans);
	        
			if(ans.contentEquals("-1")) System.out.println("No hay camino");
			System.out.println("#### Finalizado ####");
        
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato del archivo");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}

	private static boolean LeerEstaciones(String archivo) {
		try {
			// Leer las lineasDeMetro y contar cuantas lineas de metro hay
	        BufferedReader Lector = new BufferedReader(new FileReader(archivo));
	        String linea = Lector.readLine();
	        
	        // Se lee las cantida de estaciones y lineas
	        int n = Integer.parseInt(Lector.readLine());
            int m = Integer.parseInt(Lector.readLine());
            
            // Se itera sobre el archivo hasta llegar a los lados
            for(int i=0; i<n; i++) {
    	        linea = Lector.readLine();
            }
            // Se extrae informacion de los lados sobre que lineas existen 
            LinkedList<String> ciudadesa = new LinkedList<String>();
            for(int i=0; i<m; i++) {
    	        String[] helper = Lector.readLine().split("\\s+");
    	        boolean aux = true;
    	        for(String s: ciudadesa) {
    	        	if(s.contentEquals(helper[2])) {
    	        		aux = false;
    	        		break;
    	        	}
    	        }
    	        if(aux) ciudadesa.add(helper[2]);
            } 
            //  se crea un array con las ciudades y  se mapean en el archivo
        	nroLineas = ciudadesa.size();
            lineasDeMetro = new String [nroLineas];
			CambiarTexto f = new CambiarTexto();
            
            for(int i=0; i<lineasDeMetro.length; i++) {
            	lineasDeMetro[i] = ciudadesa.get(i);
				f.modifyFile(archivo, "\\b"+lineasDeMetro[i]+"\\b", Integer.toString(i));
            }           
	        Lector.close();
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	private static boolean InducirGrafo(String archivo1, GrafoDirigido grafoInducido) {
		try {
			// Leer las lineasDeMetro y contar cuantas lineas de metro hay
	        BufferedReader Lector = new BufferedReader(new FileReader(archivo1));
	        LinkedList<String> lineasInducidas = new LinkedList<String>();
	        
	        while (true) {
	            String linea = Lector.readLine();
	            if (linea == null) break;
	            lineasInducidas.add(linea);
	        }
	        Lector.close();
	        
	        for(Lado Arco: grafoInducido.lados()) {
	        	boolean auxiliar = true;
	        	for(String line : lineasInducidas) {
	        		if(lineasDeMetro[Arco.getTipo()].contentEquals(line)) {
	        			auxiliar = false;
	        		}
	        	}
	 	        
	 	        Arco aux = new Arco (Arco.getInicio(), Arco.getFin(), Arco.getTipo(), Arco.getPeso());
	 	        aux = grafoInducido.obtenerArco(Arco.getInicio(), Arco.getFin(), Arco.getTipo());
	 	        
	 	        if(auxiliar) {
	 	        	grafoInducido.eliminarArco(aux);
	 	        }
	        	
	        }
	        
	        for(Vertice vertice: grafoInducido.vertices()) {
	        //	if(grafoInducido.grado(vertice.getId())==0) grafoInducido.eliminarVertice(vertice.getId());
	        }
	        System.out.println(grafoInducido.numeroDeVertices());
	        System.out.println(grafoInducido.numeroDeLados());
	        
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private static String backtracking(GrafoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> sucesores = grafo.sucesores(verticeInicio.getId());
		int current=0;
		String caminoActual = inicio.getNombre();
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
	
	static void BTRecursivo(GrafoDirigido grafo, Arco arco, int current, String caminoActual, boolean [] visitados) { 
		// Declaracion de variables
		Vertice inicio = arco.getExtremoInicial();
		Vertice actual = arco.getExtremoFinal();
		int tipo = arco.getTipo();
		
		// Marco la estacion actual como visitada
		visitados[hashing.get(actual.getId())] = true;
		Set<Vertice> sucesores = grafo.sucesores(actual.getId());
		
		// Se actualiza el recorrido con la estacion y la linea actual
		caminoActual += " " + lineasDeMetro[tipo] + " -> " + actual.getNombre() + " " + Integer.toString(actual.getId());
		String[] helper = caminoActual.split("\\s+");
		
		// Algunas condiciones que entorpecen el recorrido
		if(actual.getId()==verticeInicio.getId() || helper.length>200 || minimo==0 ) {	return;}
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getId()==verticeFin.getId() && current < minimo) {
			minimo = current;
			caminoCompleto = caminoActual;
			System.out.println(caminoCompleto);
			System.out.println(minimo);
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
					if(i==tipo)	{ // Do nothing :)
					}else if(current+1<minimo) {
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

	private static boolean InducirGrafo(String archivo1, GrafoNoDirigido grafoInducido) {
		try {
			// Leer las lineasDeMetro y contar cuantas lineas de metro hay
	        BufferedReader Lector = new BufferedReader(new FileReader(archivo1));
	        LinkedList<String> lineasInducidas = new LinkedList<String>();
	        
	        while (true) {
	            String linea = Lector.readLine();
	            if (linea == null) break;
	            lineasInducidas.add(linea);
	        }
	        Lector.close();
	        
	        for(Lado Arista: grafoInducido.lados()) {
	        	boolean auxiliar = true;
	        	for(String line : lineasInducidas) {
	        		if(lineasDeMetro[Arista.getTipo()].contentEquals(line)) {
	        			auxiliar = false;
	        		}
	        	}
	 	        
	 	        Arista aux = new Arista (Arista.getInicio(), Arista.getFin(), Arista.getTipo(), Arista.getPeso());
	 	        aux = grafoInducido.obtenerArista(Arista.getInicio(), Arista.getFin(), Arista.getTipo());
	 	        
	 	        if(auxiliar) {
	 	        	grafoInducido.eliminarArista(aux);
	 	        }
	        	
	        }
	        
	        for(Vertice vertice: grafoInducido.vertices()) {
	        //	if(grafoInducido.grado(vertice.getId())==0) grafoInducido.eliminarVertice(vertice.getId());
	        }
	        System.out.println(grafoInducido.numeroDeVertices());
	        System.out.println(grafoInducido.numeroDeLados());
	        
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private static String backtracking(GrafoNoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> adyacentes = grafo.adyacentes(verticeInicio.getId());
		int current=0;
		String caminoActual = inicio.getNombre();
		boolean [] visitados = new boolean [nroEstaciones];
		visitados[hashing.get(verticeInicio.getId())] = true;
		caminoCompleto = "-1";
		
		// Se itera sobre sobre los vecinos del nodo inicial
		for(Vertice verticeAdj: adyacentes) {
			System.out.println(verticeAdj.getNombre());
			// Se busca cuales lines existen entre los nodos y se busca un camino entre ellos
			for(int i=0; i<nroLineas; i++) {
				if (grafo.estaArista(inicio, verticeAdj, i) && !visitados[hashing.get(verticeAdj.getId())]) {
					Arista arista = new Arista(inicio, verticeAdj, i, 0);
					arista = grafo.obtenerArista(inicio, verticeAdj, i);
					visitados[hashing.get(verticeAdj.getId())] = true;
					BTRecursivo((GrafoNoDirigido) grafo, arista, 0, caminoActual, visitados);
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		return caminoCompleto;
	}
	
	static void BTRecursivo(GrafoNoDirigido grafo, Arista arista, int current, String caminoActual, boolean [] visitados) { 
		// Declaracion de variables
		Vertice inicio = arista.getExtremo1();
		Vertice actual = arista.getExtremo2();
		int tipo = arista.getTipo();
		
		// Marista la estacion actual como visitada
		visitados[hashing.get(actual.getId())] = true;
		Set<Vertice> adyacentes = grafo.adyacentes(actual.getId());
		
		// Se actualiza el recorrido con la estacion y la linea actual
		caminoActual += " " + lineasDeMetro[tipo] + " -> " + actual.getNombre() + " " + Integer.toString(actual.getId());
		String[] helper = caminoActual.split("\\s+");
		
		// Algunas condiciones que entorpecen el recorrido
		if(actual.getId()==verticeInicio.getId() || helper.length>200 || minimo==0 ) {	return;}
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getId()==verticeFin.getId() && current < minimo) {
			minimo = current;
			caminoCompleto = caminoActual;
			System.out.println(caminoCompleto);
			System.out.println(minimo);
			return;
		}
		// Se verifica primero si tiene algun adyacente en esa misma linea
		for(Vertice verticeAdj: adyacentes) {
			// Se chequea si exite una arista del mismo tipo de linea y si es valido visitarla
			if (grafo.estaArista(actual, verticeAdj, tipo) && verticeAdj.getId() != inicio.getId() && !visitados[hashing.get(verticeAdj.getId())]) {
				visitados[hashing.get(verticeAdj.getId())] = true;
				Arista aristaAux = new Arista(actual, verticeAdj, tipo, 0);
				aristaAux = grafo.obtenerArista(actual, verticeAdj, tipo);
				BTRecursivo((GrafoNoDirigido) grafo, aristaAux, current, caminoActual, visitados);
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
					if(i==tipo)	{ // Do nothing :)
					}else if(current+1<minimo) {
						BTRecursivo((GrafoNoDirigido) grafo, aristaAux, current+1, caminoActual, visitados);
						//visitados[hashing.get(masCercanox.getId())] = false;
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
