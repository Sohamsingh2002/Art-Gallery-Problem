
# Art Gallery Problem Solver Using DCEL

Project Overview
This project implements a solution to the Art Gallery Problem using computational geometry techniques. The goal is to determine the minimum number of vertex guards required to cover a given simple polygon, based on its triangulation and dual graph. The program follows a structured sequence of operations, from polygon generation to triangulation and 3-coloring of the dual graph.


## Code Structure

1. DCEL.java
Classes: Edge, Face, Vertex, Point
Purpose: Implements the DCEL data structure, representing the edges, vertices, and faces of the polygon.

2. PolygonGen.java
Generates a simple polygon by randomly selecting n vertices and connecting them in a counter-clockwise manner.
Stores the polygon in a DCEL.

3. Trapezoidalization.java
Implements the line-sweep algorithm to decompose the polygon into trapezoids.
Uses the DCEL to manage active edges and trapezoidal segments.

4. MonotonePartition.java
Partitions the trapezoidalized polygon into monotone polygons using diagonals.

5. Triangulation.java
Triangulates monotone polygons using ear clipping and stores the resulting triangles in a DCEL.

6. DualGraph.java
Generates the dual graph of the triangulated polygon and applies the 3-coloring algorithm to determine the minimum vertex guards.

7. PointsPanel.java
Handles visualization of the entire process (polygon generation, trapezoidalization, partitioning, triangulation, and guard placement).
Draws polygons, edges, trapezoids, diagonals, triangulation, and guard positions using the paintComponent method.

8. Main.java
Main entry point for the program.
Prompts the user for the number of vertices, initializes the visualization panel, and sequentially executes the different stages of the solution.
## How to run
1. Compile the Project: Ensure you have a working Java development environment.

```bash
javac Main.java
```

2. Run the Program: Execute the main class to start the program.

```bash
java Main
```

Input: The program will prompt you for the number of vertices (n). Input a value, such as 20, to generate a polygon with 20 vertices.

Visualization:

Press the "Next" button in the GUI to progress through the stages:

- Initialize points and generate the polygon.
- Perform trapezoidalization.
- Partition into monotone polygons.
- Triangulate the polygon.
- Generate the dual graph and perform 3-coloring.
- Visualize the vertex guards and their positions.
