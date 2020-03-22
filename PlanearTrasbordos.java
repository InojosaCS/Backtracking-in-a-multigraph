import java.io.BufferedReader;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.IOException;
import java.io.*; 
import java.util.*; 

/**
 * @author Cristian Inojosa
 *
 */

public class PlanearTrasbordos {
	
	
	public static String[] ciudades;
	private static int nroLineas;
	private static int current;
	private static int minimo;
	private static int[][] tablaVisitados = new int[nroLineas+1][ciudades.length];
	
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
				return;
			}
			
			Lector.close();

			MapeoEstaciones(args[0], args[1]);
			grafo.cargarGrafo(args[0]);
			CambiarTexto b = new CambiarTexto();
			b.RecuperarArchivo(args[0], ciudades);
			
			// Busca los vertices iniciales y finales
			Collection<Vertice> vertices = grafo.vertices();
			Vertice verticeInicio = new Vertice (-1, "", -1, -1, -1);
			Vertice verticeFin = new Vertice (-1, "", -1, -1, -1);
			
			for(Vertice vertice: vertices) {
				if(vertice.getNombre().contentEquals(args[2])) {
					verticeInicio = grafo.obtenerVertice(vertice.getId());
				}if(vertice.getNombre().contentEquals(args[3])) {
					verticeFin = grafo.obtenerVertice(vertice.getId());
				}
			}
			System.out.println(verticeInicio.getId() + " " + verticeInicio.getNombre() + " " + verticeInicio.getPeso() + " ");
			System.out.println(verticeFin.getId() + " " + verticeFin.getNombre() + " " + verticeFin.getPeso() + " ");
			
			crearTablaVisitados((GrafoNoDirigido) grafo);
			
			if(grafo instanceof GrafoNoDirigido) {
				backtracking((GrafoNoDirigido)grafo, verticeInicio, verticeFin);
			}
			
            
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
			// Leer las ciudades
	        BufferedReader Lector = new BufferedReader(new FileReader(archivo1));
	        nroLineas = 0;

	        while (true) {
	            // lee las lineas del archivo 
	            String linea = Lector.readLine();
	            if (linea == null) {
	                break;
	            }
	            nroLineas++;
	        }
	        Lector.close();
	        
	        // Cambiar las ciudades
			BufferedReader Lector1 = new BufferedReader(new FileReader(archivo1));
			ciudades = new String[nroLineas];
			CambiarTexto f = new CambiarTexto();
			
			for(int i = 0; i < nroLineas; i++) {
				String[] helper = Lector1.readLine().split("\\s");
				ciudades[i] = helper[0];
				f.modifyFile(archivo, helper[0], Integer.toString(i));
			}
			Lector.close();
		
		} catch(Exception e) {
			return false;
		}
		return true;
	}
	
	private static void backtracking(GrafoNoDirigido grafo, Vertice inicio, Vertice fin) { 
		
		Set<Vertice> adyacentes = grafo.adyacentes(inicio.getId());
		int current=0;
		int minimo=Integer.MAX_VALUE;
		
		for(Vertice verticeAdj: adyacentes) {			
			System.out.println(verticeAdj.getId() + " " + verticeAdj.getNombre() + " " + verticeAdj.getPeso() + " ");
			
			for(int i=0; i<nroLineas; i++) {
				boolean [] visitado = new boolean[ciudades.length];
				
				if (grafo.estaArista(inicio, verticeAdj, i) ) {
					Arista arista = new Arista(inicio, verticeAdj, i, 0);
					arista = grafo.obtenerArista(inicio, verticeAdj, i);
					BTRecursivo((GrafoNoDirigido) grafo, arista, current, minimo, fin.getNombre());
				}
			}
		}
		System.out.println(minimo);
		
		
	}
	
	private static void BTRecursivo(GrafoNoDirigido grafo, Arista arista, int current, int minimo, String destino) { 
		
		Vertice inicio = arista.getExtremo1();
		Vertice fin = arista.getExtremo2();
		int tipo = arista.getTipo();
		
		System.out.println(inicio.getNombre()+" "+fin.getNombre()+" "+tipo);
		
		Set<Vertice> adyacentes = grafo.adyacentes(fin.getId());

		if( inicio.getNombre().contentEquals(destino) ) {
			minimo = current < minimo ? current : minimo;
			return;
		}if(adyacentes.size() == 0 || current >= minimo) {
			return;
		}
		
		
		for(Vertice verticeAdj: adyacentes) {
			for(int i=0; i<nroLineas; i++) {
				if (grafo.estaArista(fin, verticeAdj, tipo)) {
					Arista aristaAux = new Arista(fin, verticeAdj, tipo, 0);
					aristaAux = grafo.obtenerArista(fin, verticeAdj, tipo);
					BTRecursivo((GrafoNoDirigido) grafo, aristaAux, current, minimo, destino);
				}
			}
		}
		
		for(Vertice verticeAdj: adyacentes) {
			for(int i=0; i<nroLineas; i++) {
				if (grafo.estaArista(fin, verticeAdj, i)) {
					Arista aristaAux = new Arista(fin, verticeAdj, i, 0);
					aristaAux = grafo.obtenerArista(fin, verticeAdj, i);
					BTRecursivo((GrafoNoDirigido) grafo, aristaAux, current+1, minimo, destino);
				}
			}
		}
	}
	
	private static void crearTablaVisitados(GrafoNoDirigido grafo) {
		
		int i = 0;
		
		for(Vertice vertice: grafo.vertices()) {
			tablaVisitados [0][i] = vertice.getId();
			i++;
			
			for(int j=1; j<nroLineas+1; j++) {
				tablaVisitados[i][j] = 0;
			}
		}
		
	}
	
	private static boolean visitado(GrafoNoDirigido grafo, Vertice v, int tipo) {
		int i=0;
		
		for(Vertice vertice: grafo.vertices()) {
			if(vertice.getNombre().contentEquals(v.getNombre())) {
				break;
			}
			i++;
		}
		return tablaVisitados[i][tipo+1]==1;
	}

}