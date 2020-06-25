import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*; 

/**
 * @author Cristian Inojosa 17-10285
 * @author Alejandro Salazar 16-11080
 */

public class PlanearTrasbordos {

	/**
	 * Variables que guarda los nombres de las lineas de metro
	 */
	private static String[] lineasDeMetro;
	
	/**
	 * Variables que guardan el numero de lineas y estaciones
	 */
	private static int nroLineas, nroEstaciones;
	
	/**
	 * Variable que representa la cantidad minima de trasbordos para cualquier etapa de la arborescencia 
	 * del recorrido del grafo, sirve para acotar el numero de trasbordos posibles durante el backtracking
	 * Se inicializa en 7 para optimizar el backtracking, ya que en Caracas no hay ninguna ruta que tome mas de 7 
	 * trasbordos
	 */
	public static int minimo = 7;
	
	/**
	 * Variable se va actualizando en cada etapa del recorrido del grafo
	 */
	private static String caminoCompleto;
	
	/**
	 * Variable que sirve para mapear N id's de N vertices con una secuencia de {0,2,3...;N-1} 
	 * se usa para marcar los vertices como visitados en O(n) en complejidad de espacio
	 */
	private static HashMap<Integer, Integer> hashing = new HashMap <Integer, Integer>();
	
	/**
	 * Variables que marcan los vertices de inicio y fin de camino que estamos buscando
	 */
	private static Vertice verticeInicio, verticeFin;

    
	/**
     * Llama a las otras funciones para cargar eLa Graformacion y hacer el backtracking
     * (De 18 casos de prueba, generados por los autores, el tiempo maximo esperado por 
     * el algoritmo es alredor de dos minutos en dos casos, en los otros 16 fue instantaneo;
     * probado en una laptop con Windows10, procesador Intel Core i5-8265U @ 1.60GHz 1.80GHz y 8gb RAM)
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
			
			leerEstaciones(args[0]);
			grafo.cargarGrafo(args[0]);
			grafoInducido = grafo;
			CambiarTexto auxiliar = new CambiarTexto();
			auxiliar.recuperarArchivo(args[0], lineasDeMetro);
			
			// Busca los vertices iniciales y finales
			Collection<Vertice> vertices = grafo.vertices();
			nroEstaciones = vertices.size();
			
			verticeInicio = new Vertice (-1, "", -1, -1, -1);
			verticeFin = new Vertice (-1, "", -1, -1, -1);
			
			int i=0;
			for(Vertice vertice: vertices) {
				hashing.put(vertice.getId(), i); // Mapeo de los ids
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
		
			System.out.println("El programa termina cuando aparezca el mensaje de finalizado para cada grafo, por favor espere \n");

			PlanearRutas rutas = new PlanearRutas(lineasDeMetro, nroLineas, nroEstaciones, minimo, hashing, verticeInicio, verticeFin);
			
			String respuesta1, respuesta2;
			
			// Llamado al backtracking
			if(grafo instanceof GrafoNoDirigido) {
				respuesta2 = rutas.backtracking((GrafoNoDirigido)grafo, verticeInicio, verticeFin);
				inducirGrafo(args[1], (GrafoNoDirigido) grafoInducido);
				PlanearRutas.minimo = 7;
				minimo = 7;
				respuesta1 = rutas.backtracking((GrafoNoDirigido)grafo, verticeInicio, verticeFin);
        	}else {
        		respuesta2 = rutas.backtracking((GrafoDirigido)grafo, verticeInicio, verticeFin);
				inducirGrafo(args[1], (GrafoDirigido) grafoInducido);
				PlanearRutas.minimo = 7;
				minimo = 7;
				respuesta1 = rutas.backtracking((GrafoDirigido)grafoInducido, verticeInicio, verticeFin);
        	}
			
			// Se le da el formato a la respuesta y se imprime
			if(respuesta1.contentEquals("-1"))  
				System.out.println("No hay camino en el grafo inducido entre " + verticeInicio.getNombre() + " y " + verticeFin.getNombre()); 
			else {
				String[] Auxiliar = respuesta1.split("\\s+");
				String numeroMinimoTrasbordos = Auxiliar[0], ultimaEstacionCambiada = Auxiliar[1] + " " + Auxiliar[2], ultimaLineaCambiada = Auxiliar[3];
				System.out.println("Cuando el grafo esta inducido");
				for(i=3; i<Auxiliar.length; i+=3) {
					if(!Auxiliar[i].contentEquals(ultimaLineaCambiada)) {
						System.out.println("Tome la linea " + ultimaLineaCambiada + " desde " + ultimaEstacionCambiada + " hasta " + Auxiliar[i-2]+" "+ Auxiliar[i-1]);
						ultimaEstacionCambiada = Auxiliar[i-2]+" "+ Auxiliar[i-1];
						ultimaLineaCambiada = Auxiliar[i];
					}
				}
				System.out.println("Tome la linea " + ultimaLineaCambiada + " desde " + ultimaEstacionCambiada + " hasta " +  verticeFin.getId() + " " +verticeFin.getNombre());
				System.out.println("Trasbordos totales: " + numeroMinimoTrasbordos);
			}
			
			System.out.print("\n"); // Por pulcritud
			
			if(respuesta2.contentEquals("-1"))  
				System.out.println("No hay camino en el grafo " + verticeInicio.getNombre() + " y " + verticeFin.getNombre()); 
			else {
				String[] Auxiliar = respuesta2.split("\\s+");
				String numeroMinimoTrasbordos = Auxiliar[0], ultimaEstacionCambiada = Auxiliar[1] + " " + Auxiliar[2], ultimaLineaCambiada = Auxiliar[3];
				System.out.println("Cuando todas las lineas estan operativas");
				for(i=3; i<Auxiliar.length; i+=3) {
					if(!Auxiliar[i].contentEquals(ultimaLineaCambiada)) {
						System.out.println("Tome la linea " + ultimaLineaCambiada + " desde " + ultimaEstacionCambiada + " hasta " + Auxiliar[i-2]+" "+Auxiliar[i-1]);
						ultimaEstacionCambiada = Auxiliar[i-2]+" "+Auxiliar[i-1];
						ultimaLineaCambiada = Auxiliar[i];
					}
				}
				System.out.println("Tome la linea " + ultimaLineaCambiada + " desde " + ultimaEstacionCambiada + " hasta " + verticeFin.getId() + " " +verticeFin.getNombre());
				System.out.println("Trasbordos totales: " + numeroMinimoTrasbordos);
			}
			
			System.out.println("\n#### Finalizado ####");
			
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error en el formato del archivo");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}

	/**
     * Lee cuales y cuantas lineas de metro hay en el archivo proporcionado por el usuario y mapea los nombres de 
     * las lineas de metro con un entero, modifica el archivo cambiando los nombre de las lineas por los entero 
     * recien mapeados (Luego se devuelve el archivo a su estado original)
     * @param Archivo Archivo con la nformacion que el usuario proporciona
     */
	private static boolean leerEstaciones(String archivo) {
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
			CambiarTexto cambio = new CambiarTexto();
            
            for(int i=0; i<lineasDeMetro.length; i++) {
            	lineasDeMetro[i] = ciudadesa.get(i);
				cambio.modificarArchivo(archivo, "\\b"+lineasDeMetro[i]+"\\b", Integer.toString(i));
            }           
	        Lector.close();
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	/**
     * Lee cuales lineas de metro se conservaran, luego itera sobre los lados de una copia grafo completo y los que no sean de esas lineas
     * son eliminados, por efectos practicos no se eliminan los nodos para evitar lanzar una excepcion en caso que el pInicio o pfin
     * este en uno de ellos y se pueda usar el mismo algoritmo para el grafo y el grafo inducido. 
     * @param Archivo1 Archivo con la informacion sobre que lineas se conservarn
     * @param grafoInducido Es una grafo igual al grafo completo; al cual se le eliminaran los Arcos que no sean de la lineas validas
     * @return True si lee la informacion correctamente, falso si no
     */
	private static boolean inducirGrafo(String archivo1, GrafoDirigido grafoInducido) {
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
	        
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	/**
     * Lee cuales lineas de metro se conservaran, luego itera sobre los lados de una copia grafo completo y los que no sean de esas lineas
     * son eliminados, por efectos practicos no se eliminan los nodos para evitar lanzar una excepcion en caso que el pInicio o pfin
     * este en uno de ellos y se pueda usar el mismo algoritmo para el grafo y el grafo inducido. 
     * @param Archivo1 Archivo con la informacion sobre que lineas se conservarn
     * @param grafoInducido Es una grafo igual al grafo completo; al cual se le eliminaran la aristas que no sean de la lineas validas
     * @return True si lee la informacion correctamente, falso si no
     */
	private static boolean inducirGrafo(String archivo1, GrafoNoDirigido grafoInducido) {
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
	        //System.out.println(grafoInducido.numeroDeLados());
	        //System.out.println(grafoInducido.numeroDeVertices());
		} catch(Exception e) {
			return false;
		}
		return true;
	}

}