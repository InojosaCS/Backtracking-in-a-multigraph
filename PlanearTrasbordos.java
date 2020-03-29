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
			
			LeerEstaciones(args[0]);
			//MapeoEstaciones(args[0], args[1]);
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

	private static boolean LeerEstaciones(String archivo) {
		try {
			// Leer las lineasDeMetro y contar cuantas lineas de metro hay
	        BufferedReader Lector = new BufferedReader(new FileReader(archivo));
	        String linea = Lector.readLine();
	        
	        int n = Integer.parseInt(Lector.readLine());
            int m = Integer.parseInt(Lector.readLine());
            
            for(int i=0; i<n; i++) {
    	        linea = Lector.readLine();
            }
            
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
    	        if(aux) {
    	        	ciudadesa.add(helper[2]);
    	        }
            } 
            
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
	
	private static String backtracking(GrafoNoDirigido grafo, Vertice inicio, Vertice fin) { return "";	}

	private static String backtracking(GrafoDirigido grafo, Vertice inicio, Vertice fin) { 
		// Declaracion de variables
		Set<Vertice> sucesores = grafo.sucesores(verticeInicio.getId());
		int current=0;
		String caminoActual = inicio.getNombre();
		
		// Heuristica
		Vertice masCercano = new Vertice (-1, "", -1, -1, -1);
		Double distanciaMinima = Double.MAX_VALUE;
		boolean [] visitados = new boolean [nroEstaciones];
		visitados[hashing.get(verticeInicio.getId())] = true;
		
		for(Vertice verticeAdj: sucesores) {
			if( Math.sqrt(Math.pow((verticeAdj.getX() - verticeFin.getX()),2) + Math.pow((verticeAdj.getY() - verticeFin.getY()),2)) < distanciaMinima){
				masCercano = verticeAdj;
				distanciaMinima = Math.sqrt(Math.pow((verticeAdj.getX() - verticeFin.getX()),2) + Math.pow((verticeAdj.getY() - verticeFin.getY()),2));
			}
		}
		/*
		System.out.println(masCercano.getNombre());
		for(int i=0; i<nroLineas; i++) {
			if (grafo.estaArco(inicio, masCercano, i)) {
				Arco arcoU = new Arco(inicio, masCercano, i, 0);
				arcoU = grafo.obtenerArco(inicio, masCercano, i);
				visitados[hashing.get(masCercano.getId())] = true;
				BTRecursivo((GrafoDirigido) grafo, arcoU, 0, caminoActual, visitados);
				visitados[hashing.get(masCercano.getId())] = false;
			}
		}
*/
		//visitados[hashing.get(masCercano.getId())] = true;
		
		// Se itera sobre sobre los vecinos del nodo inicial
		for(Vertice verticeAdj: sucesores) {
			System.out.println(verticeAdj.getNombre());
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

		visitados[hashing.get(actual.getId())] = true;
		
		Set<Vertice> sucesores = grafo.sucesores(actual.getId());
		//System.out.println(inicio.getNombre()+"->"+actual.getNombre()+" "+lineasDeMetro[tipo]);
		
		caminoActual += " " + lineasDeMetro[tipo] + " -> " + actual.getNombre() + " " + Integer.toString(actual.getId());
		String[] helper = caminoActual.split("\\s+");
		
		if(actual.getId()==verticeInicio.getId() || helper.length>200 || minimo==0 ) {	return;}
		
		// si llega al destino entonces acaba el backtracking y asigna los nuevos valores
		if( actual.getId()==verticeFin.getId() && current < minimo) {
			minimo = current;
			caminoCompleto = caminoActual;
			System.out.println(caminoCompleto);
			System.out.println(minimo);
			System.out.println(helper.length);
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
				}
		}
		
		Vertice masCercanox = new Vertice (-1, "", -1, -1, -1);
		Double distanciaMinima = Double.MAX_VALUE;
		
		for(Vertice verticeAdj: sucesores) {
			if( Math.sqrt(Math.pow((verticeAdj.getX() - verticeFin.getX()),2) + Math.pow((verticeAdj.getY() - verticeFin.getY()),2)) < distanciaMinima){
				masCercanox = verticeAdj;
				distanciaMinima = Math.sqrt(Math.pow((verticeAdj.getX() - verticeFin.getX()),2) + Math.pow((verticeAdj.getY() - verticeFin.getY()),2));
			}
		}
		
		//System.out.println(masCercano.getNombre());
		/*
		for(int x=0; x<nroLineas;x++) {
			if (grafo.estaArco(actual, masCercanox, x) && masCercanox.getId() != inicio.getId() && !visitados[hashing.get(masCercanox.getId())]) {
				Arco arcoA = new Arco(actual, masCercanox, x, 0);
				arcoA = grafo.obtenerArco(actual, masCercanox, x);
				visitados[hashing.get(masCercanox.getId())] = true;
				if(x==tipo)	{
					BTRecursivo((GrafoDirigido) grafo, arcoA, current, caminoActual, visitados);
				}
				else if(current+1<minimo) {
					BTRecursivo((GrafoDirigido) grafo, arcoA, current+1, caminoActual, visitados);
					//visitados[hashing.get(masCercanox.getId())] = false;
				}
				//visitados[hashing.get(masCercanox.getId())] = false;	
			}	
		}	
		*/
		for(Vertice verticeAdj: sucesores) {
			// Se itera sobre todos los sucesores del nodo actual 
			for(int i=0; i<nroLineas; i++) {
				// Se chequea si exite una arco por cualquier tipo de linea y si 
				// es valido visitarla (esto es, que no haya sido visitado ese nodo por la misma linea)
				if (grafo.estaArco(actual, verticeAdj, i) && current + 1 < minimo  && !visitados[hashing.get(verticeAdj.getId())]) {
					visitados[hashing.get(verticeAdj.getId())] = true;
					Arco arcoAux = new Arco(actual, verticeAdj, i, 0);
					arcoAux = grafo.obtenerArco(actual, verticeAdj, i);
					if(i==tipo)	{
					}
					else if(current+1<minimo) {
						BTRecursivo((GrafoDirigido) grafo, arcoAux, current+1, caminoActual, visitados);
						//visitados[hashing.get(masCercanox.getId())] = false;
					}
					visitados[hashing.get(verticeAdj.getId())] = false;
				}
			}
		}
		visitados[hashing.get(actual.getId())] = true;
		return;
	}
	
}
