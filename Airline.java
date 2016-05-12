import java.util.*;
import java.io.*;
import java.lang.*;

public class Airline {

  public static String[] locations;
  public static EdgeWeightedDigraph edge_digraph;
  public static ArrayList<DirectedEdge> edge_list = new ArrayList<DirectedEdge>();

  public static void main(String[] args) throws IOException {

    String filename = "";
    int choice = 0;
    String start = "";
    String end = "";
    int price = 0;
    int mileage = 0;
    int s;
    int e;

///////////////////////////////////////////////////////////////////////////////

    Scanner input = new Scanner(System.in);
    System.out.print("Enter a file: ");
    filename = input.nextLine();
    File file = new File(filename);

    try {
      Scanner filescan = new Scanner(file);
      int i = Integer.parseInt(filescan.nextLine());
      locations = new String[i+1];
      locations[0] = "null";
      for (int j = 1; j <= i; j++) {
        locations[j] = filescan.nextLine();
      }
      edge_digraph = new EdgeWeightedDigraph(i+1);
      while (filescan.hasNext()) {
        String line = filescan.nextLine();
        String[] airline_data = line.split(" ");
        s = Integer.parseInt(airline_data[0]);
        e = Integer.parseInt(airline_data[1]);
        mileage = Integer.parseInt(airline_data[2]);
        double priced = Double.parseDouble(airline_data[3]);
        DirectedEdge e1 = new DirectedEdge(s, e, mileage, priced);
        DirectedEdge e2 = new DirectedEdge(e, s, mileage, priced);
        edge_list.add(e1);
        edge_list.add(e2);
        edge_digraph.addEdge(e1);
        edge_digraph.addEdge(e2);
      }
    }
    catch (IOException ex) {
      System.out.println(ex);
    }

///////////////////////////////////////////////////////////////////////////////

    while (choice != 9) {

      System.out.println();
      System.out.println("------------ Airline Menu ------------");
      System.out.println();
      System.out.println("1. Display all airline routes");
      System.out.println("2. Display minimum spanning tree");
      System.out.println("3. Display shortest route by mileage");
      System.out.println("4. Display shortest route by price");
      System.out.println("5. Display shortest route by hops");
      System.out.println("6. Display routes below set price");
      System.out.println("7. Add route");
      System.out.println("8. Remove route");
      System.out.println("9. Save and quit program");
      System.out.println();
      System.out.println("--------------------------------------");
      System.out.println();
      System.out.print("Please select a numeric option: ");
      choice = input.nextInt();
      input.nextLine();
      System.out.println();

///////////////////////////////////////////////////////////////////////////////

      switch (choice) {

        case 1: // Display all airline routes

          for (int i = 1; i < locations.length; i++) {
            Iterator<DirectedEdge> iterate1 = edge_digraph.adj(i).iterator();
            if (iterate1 != null) {
              while (iterate1.hasNext()) {
                System.out.println(routeConvert(iterate1.next()));
              }
            }
          }

          break;

///////////////////////////////////////////////////////////////////////////////

        case 2: // Display minimum spanning tree

          PrimMST tree = new PrimMST(edge_digraph);
          Iterator<DirectedEdge> x = tree.edges().iterator();

          while (x.hasNext()) {
            DirectedEdge edge = x.next();
            System.out.println(locations[edge.to()] + ", " + locations[edge.from()] + " : " + edge.weight());
          }

          break;

///////////////////////////////////////////////////////////////////////////////

        case 3: // Display shortest route by mileage

          System.out.println("Enter start city: ");
          start = input.nextLine();

          System.out.println("Enter end city: ");
          end = input.nextLine();

          s = stringToInt(start);
          e = stringToInt(end);

          DijkstraSP dij = new DijkstraSP(edge_digraph, s, true);
          Iterator<DirectedEdge> iterate2 = dij.pathTo(e).iterator();

          System.out.println("Shortest distance from " + locations[s] + " to " + locations[e] + " is " + iterate2.next().weight());
          System.out.println("Path with edges (in reverse order):");

          String z = "";

          while (iterate2.hasNext()) {
            DirectedEdge edge1 = iterate2.next();
            z = locations[edge1.to()] + " " + edge1.weight() + " " + z;
          }

          z += locations[s];

          System.out.println(z);

          break;

///////////////////////////////////////////////////////////////////////////////

        case 4: // Display shortest route by price

          System.out.println("Enter start city: ");
          start = input.nextLine();

          System.out.println("Enter end city: ");
          end = input.nextLine();

          s = stringToInt(start);
          e = stringToInt(end);

          DijkstraSP dij1 = new DijkstraSP(edge_digraph, s, false);
          Iterator<DirectedEdge> iterate3 = dij1.pathToTwo(e).iterator();

          System.out.println("Cheapest from " + locations[s] + " to " + locations[e] + " is " + (iterate3.next().weight()));
          System.out.println("Path with edges (in reverse order):");

          String w = "";

          while (iterate3.hasNext()) {
            DirectedEdge edge2 = iterate3.next();
            w = locations[edge2.to()] + " " + edge2.weightTwo() + " " + w;
          }

          w += locations[s];

          System.out.println(w);

          break;

///////////////////////////////////////////////////////////////////////////////

        case 5: // Display shortest route by hops

          System.out.println("Enter start city: ");
          start = input.nextLine();

          System.out.println("Enter end city: ");
          end = input.nextLine();

          s = stringToInt(start);
          e = stringToInt(end);

          BreadthFirstPaths bfp = new BreadthFirstPaths(edge_digraph, s);
          Iterable<Integer> it = bfp.pathTo(e);

          String[] path = it.toString().split(" ");
          System.out.println("Hops from " + locations[s] + " to " + locations[e] + " is: " + (path.length - 1));
          System.out.println("Path (In reverse order) ");

          for (int i = path.length - 1; i >= 0; i--){
            System.out.print(locations[Integer.parseInt(path[i])] + " ");
          }

          break;

///////////////////////////////////////////////////////////////////////////////

        case 6: // Display routes below set price

          System.out.println("Enter price: ");
          price = input.nextInt();

          ArrayList<StringBuilder> routes = new ArrayList<>();

          for (int i = 1; i < locations.length; i++) {
            for (int j = 1; j < locations.length; j++) {
              if (j == i) {
                j++;
                if (j == locations.length) {
                  break;
                }
              }
              ArrayList<ArrayList<Integer>> routes_list = paths(i,j);
              for (int a = 0; a < routes_list.size(); a++) {
                Iterator<Integer> iterate = routes_list.get(a).iterator();
                int total = 0;
                s = iterate.next();
                e = iterate.next();
                StringBuilder out = new StringBuilder(locations[s]);
                while (true) {
                  Iterator<DirectedEdge> iter = edge_digraph.adj(s).iterator();
                  while (iter.hasNext()) {
                    DirectedEdge edge3 = iter.next();
                    if (edge3.to() == e) {
                      total += edge3.weightTwo();
                      out.append(" " + (int)edge3.weightTwo() + " " + locations[s]);
                    }
                  }
                  if (!iterate.hasNext()) {
                    break;
                  }
                  s = e;
                  e = iterate.next();
                }
                if (total <= price) {
                  StringBuilder output = new StringBuilder();
                  output.append("Cost: " + total + " Path (reserved): " + out.toString() + "\n");
                  if (!routes.contains(output)) {
                    routes.add(output);
                    System.out.println(output);
                  }
                }
              }
            }
          }

          break;

///////////////////////////////////////////////////////////////////////////////

        case 7: // Add route

          System.out.println("Enter start city: ");
          start = input.nextLine();

          System.out.println("Enter end city: ");
          end = input.nextLine();

          s = stringToInt(start);
          e = stringToInt(end);

          System.out.println("Enter mileage: ");
          mileage = input.nextInt();

          System.out.println("Enter price: ");
          price = input.nextInt();

          edge_digraph.addEdge(new DirectedEdge(s,e,mileage,price));
          edge_digraph.addEdge(new DirectedEdge(e,s,mileage,price));

          System.out.println("Route has been added...");

          break;

///////////////////////////////////////////////////////////////////////////////

        case 8: // Remove route

          System.out.println("Enter start city: ");
          start = input.nextLine();

          System.out.println("Enter end city: ");
          end = input.nextLine();

          s = stringToInt(start);
          e = stringToInt(end);

          Iterator<DirectedEdge> iterate4 = edge_digraph.adj[s].iterator();
          Stack<DirectedEdge> stack = new Stack<>();

          while (iterate4.hasNext()) {
            DirectedEdge edge4 = iterate4.next();
            if (edge4.to() == e) {
              continue;
            }
            stack.push(edge4);
          }

          edge_digraph.adj[s] = new Bag<>();

          while (!stack.isEmpty()) {
            edge_digraph.adj[s].add(stack.pop());
          }

          iterate4 = edge_digraph.adj[e].iterator();

          stack = new Stack<>();

          while (iterate4.hasNext()) {
            DirectedEdge edge5 = iterate4.next();
            if (edge5.to() == s) {
              continue;
            }
            stack.push(edge5);
          }

          edge_digraph.adj[e] = new Bag<>();

          while (!stack.isEmpty()) {
            edge_digraph.adj[e].add(stack.pop());
          }

          break;

///////////////////////////////////////////////////////////////////////////////

        case 9: // Save and quit program

          System.out.println("Saving to input file and quitting program...");
          System.out.println();

          try {

            FileWriter file_writer = new FileWriter(file);

            file_writer.write(locations.length - 1 + "\n");

            for (String location: locations) {
              if (location.equals("null")) {
                continue;
              }
              file_writer.write(location + "\n");
            }

            ArrayList<DirectedEdge> route_list = new ArrayList<>();

            for (int i = 1; i < locations.length; i++) {
              Iterator<DirectedEdge> iterate5 = edge_digraph.adj(i).iterator();
              if (iterate5 == null) {
                continue;
              }
              while (iterate5.hasNext()) {
                DirectedEdge edge6 = iterate5.next();
                s = edge6.from();
                e = edge6.to();
                mileage = (int)edge6.weight();
                double prices = edge6.weightTwo();
                if (route_list.size() == 0) {
                  file_writer.write(s + " " + e + " " + mileage + " " + prices + "\n");
                }
                int j = -1;
                for (DirectedEdge route: route_list) {
                  j++;
                  if (route.to() == s && route.from() == e) {
                    break;
                  }
                  if (j == route_list.size()-1) {
                    file_writer.write(s + " " + e + " " + mileage + " " + prices + "\n");
                  }
                }
                route_list.add(edge6);
              }
            }
            file_writer.close();
            System.exit(0);
          }

          catch (IOException exe) {
            System.out.println(exe);
          }

          break;

///////////////////////////////////////////////////////////////////////////////

        default: // Reset menu on invalid selection

          System.out.println();
          System.out.println("Please enter a valid menu option...");

          break;
      }
    }
  }

  public static int stringToInt(String str) {
    for (int i = 1; i < locations.length; i++) {
      String x = locations[i];
      if (x.equalsIgnoreCase(str)) {
        return i;
      }
    }
    return 0;
  }

  private static String routeConvert(DirectedEdge edge) {
      return "Cost: $" + edge.weightTwo() + " Path: " + locations[edge.from()] + " " + edge.weight() + " " + locations[edge.to()];
  }

  public static DirectedEdge route(int start, int end) {
    for (DirectedEdge edge : edge_list) {
      if (start == edge.from() && end == edge.to()) {
        return edge;
      }
    }
    return null;
  }


  public static ArrayList<ArrayList<Integer>> paths(int start, int end) {
      ArrayList<ArrayList<Integer>> path_list = new ArrayList<ArrayList<Integer>>();
      recurse(start, end, path_list, new LinkedHashSet<Integer>());
      return path_list;
  }

  private static void recurse(int x, int end, ArrayList<ArrayList<Integer>> path_list, LinkedHashSet<Integer> p) {
    p.add(x);
    if (x == end) {
      path_list.add(new ArrayList<Integer>(p));
      p.remove(x);
      return;
    }
    ArrayList<Integer> edge_list = new ArrayList<>();
    Iterator<DirectedEdge> iterate6 = edge_digraph.adj(x).iterator();
    while(iterate6.hasNext()) {
      edge_list.add(iterate6.next().to());
    }
    for (int edg : edge_list) {
      if (!p.contains(edg)) {
        recurse(edg, end, path_list, p);
      }
    }
    p.remove(x);
  }
}
