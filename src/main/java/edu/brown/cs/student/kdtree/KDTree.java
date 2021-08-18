package edu.brown.cs.student.kdtree;

import edu.brown.cs.student.maps.Node;
import edu.brown.cs.student.stars.HasCoordinates;
import edu.brown.cs.student.stars.HasCoordinatesComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Class KDTree models a binary search tree with k dimensions.
 *
 * @param <T> generic HasCoordinates
 */
public class KDTree<T extends HasCoordinates> {
  private KDTreeNode<T> root;
  private int numDimensions;
  private final List<T> radiusReturnList;
  private final List<T> neighborReturnList;
  private PriorityQueue<KDTreeNode<T>> radiusPQ;
  private PriorityQueue<KDTreeNode<T>> neighborsPQ;
  private final HashMap<KDTreeNode<T>, Double> distanceMap;
  private static final double RANDOM_CHANCE = 0.50;

  /**
   * Constructor builds a tree given a list of HasCoordinate objects and an int of dimensions.
   *
   * @param list of objects that extend HasCoordinates interface.
   * @param k    the number of dimensions that object has.
   */
  public KDTree(List<T> list, int k) {
    //initializing global variable for the radius search
    radiusReturnList = new ArrayList<>();
    neighborReturnList = new ArrayList<>();
    distanceMap = new HashMap<>(); // map represents distances

    //storing a copy of the input list
    List<T> inputList = new ArrayList<>(list);
    if (inputList.isEmpty()) {
      System.out.println("ERROR: input list is empty, cannot build tree.");
      return;
    }
    numDimensions = k;

    //find the root
    inputList.sort(new HasCoordinatesComparator<>(0));
    root = new KDTreeNode<>(inputList.get(inputList.size() / 2), null, null, 0);
    //build tree
    this.build(inputList, root);
  }

  /**
   * Build method builds the tree using a recursive call on a current node, given an input list.
   *
   * @param sortedList of HasCoordinates objects.
   * @param parent     a KDTreeNode to iterate on.
   */
  public void build(List<T> sortedList, KDTreeNode<T> parent) {
    int pivot = sortedList.indexOf(parent.getElement());
    //split the input sortedList into left and right copy arrays
    List<T> lessThan = new ArrayList<>(sortedList.subList(0, pivot));
    List<T> greaterThan = new ArrayList<>(sortedList.subList(pivot + 1, sortedList.size()));
    //construct left subtree
    if (!lessThan.isEmpty()) {
      int leftPiv = lessThan.size() / 2;
//      int leftPiv = findPivot(lessThan);
      KDTreeNode<T> leftChild =
          new KDTreeNode<>(lessThan.get(leftPiv), null, null, parent.getDepth() + 1);
      parent.setLeftChild(leftChild);
      //sort based on the next dimension
      lessThan.sort(new HasCoordinatesComparator<>(leftChild.getDepth() % numDimensions));
      if (lessThan.size() > 1) {
        this.build(lessThan, leftChild); // recursive call on a sorted list, parent
      }
    }
    //construct right subtree
    if (!greaterThan.isEmpty()) {
      int rightPiv = greaterThan.size() / 2;
//      int rightPiv = findPivot(greaterThan);
      KDTreeNode<T> rightChild =
          new KDTreeNode<>(greaterThan.get(rightPiv), null, null, parent.getDepth() + 1);
      parent.setRightChild(rightChild);
      //sort based on the next dimension
      greaterThan.sort(new HasCoordinatesComparator<>(rightChild.getDepth() % numDimensions));
      if (greaterThan.size() > 1) {
        this.build(greaterThan, rightChild);
      }
    }
  }

  /**
   * Radius method collects all the HasCoordinate objects within double radius of a given target.
   *
   * @param radius a double value of the search bound.
   * @param target a KDTree node or target on the plan to search around.
   * @return collection of objects within radius to target
   */
  public List<T> radius(double radius, T target) {
    //convert HasCoordinates Object to KDTreeNode to be applicable for search
    KDTreeNode<T> targetNode =
        new KDTreeNode<>(target, null, null, 0);
    //reset the distance map and pq for reuse
    distanceMap.clear();
    radiusReturnList.clear();
    radiusPQ = new PriorityQueue<>(3, (a, b) -> {
      if (distanceMap.get(a) < distanceMap.get(b)) {
        return -1;
      } else if (distanceMap.get(a) > distanceMap.get(b)) {
        return 1;
      } else {
        return 0;
      }
    });
    //search the tree using a recursive call
    this.radiusSearch(radius, root, targetNode);
    while (!radiusPQ.isEmpty()) {
      radiusReturnList.add(radiusPQ.remove().getElement());
    }
    return radiusReturnList;
  }

  /**
   * Recursive call for radius method.
   *
   * @param radius a double representation of the search bound.
   * @param curr   the current node of the KDTree.
   * @param target the node point we are searching around.
   */
  public void radiusSearch(double radius, KDTreeNode<T> curr, KDTreeNode<T> target) {
    //check if curr is within radius
    distanceMap.put(curr, this.euclideanDistance(curr, target)); //store distance
    if ((distanceMap.get(curr) <= radius)
        && (!curr.getElement().getName().equals(target.getElement().getName()))) {
      radiusPQ.add(curr);
    }
    Double currAxisValue = curr.getElement().getCoordinate(curr.getDepth() % numDimensions);
    Double targetAxisValue = target.getElement().getCoordinate(target.getDepth() % numDimensions);
    //case in bound of radius from target recur on both children
    if (Math.abs(currAxisValue - targetAxisValue) <= radius) {
      if (!(curr.getLeftChild() == null)) {
        this.radiusSearch(radius, curr.getLeftChild(), target);
      }
      if (!(curr.getRightChild() == null)) {
        this.radiusSearch(radius, curr.getRightChild(), target);
      }
    } else { //recur only on left child
      if (!(curr.getLeftChild() == null)) {
        this.radiusSearch(radius, curr.getLeftChild(), target);
      }
    }
  }

  /**
   * Neighbors method finds the k closest neighbors to a given target.
   *
   * @param k      an integer putting a restraint on the neighbors search.
   * @param target the target point to look around.
   * @param name   the target point to look around.
   * @return collection of k closest neighbors.
   */
  public List<T> neighbors(int k, T target, Boolean name) {
    //convert HasCoordinates Object to KDTreeNode to be applicable for search
    KDTreeNode<T> targetNode =
        new KDTreeNode<>(target, null, null, 0);
    //reset the distance map and pq for reuse
    distanceMap.clear();
    neighborReturnList.clear();
    if (k == 0) {
      return new ArrayList<>();
    }
//    //set up a new priority queue with reverse order
//    //initial capacity has to be > 1
//    int initialPqCapacity = k;
//    if (k < 1) {
//      initialPqCapacity = 1;
//    }
//    //setting up priority queue in reverse order
//    neighborsPQ = new PriorityQueue<>(initialPqCapacity, (a, b) -> {
//      if (distanceMap.get(a) < distanceMap.get(b)) {
//        return 1;
//      } else if (distanceMap.get(a) > distanceMap.get(b)) {
//        return -1;
//      } else {
//        return 0;
//      }
//    });
//    //search the tree using a recursive call
//    this.neighborsSearch(k, root, targetNode);
    ArrayList<KDTreeNode<T>> neighborsList = new ArrayList<>();
    if (name) {
      //search
      searchNeighbors(k + 1, root, targetNode, neighborsList);
//      while (!neighborsPQ.isEmpty()) {
//        T neighbor = neighborsPQ.remove().getElement();
//        if (!neighbor.getName().equals(target.getName())) {
//          neighborReturnList.add(neighbor);
//        }
//      }
      for (KDTreeNode<T> n : neighborsList) {
        if (n.getElement().getName() != target.getName()) {
          neighborReturnList.add(n.getElement());
        }
      }
    } else {
      searchNeighbors(k, root, targetNode, neighborsList);
      //edge case
//      if (neighborsPQ.size() == 1) {
//        neighborReturnList.add(neighborsPQ.remove().getElement());
//      } else {
//        //remove farthest
//        neighborsPQ.remove();
//        while (!neighborsPQ.isEmpty()) {
//          neighborReturnList.add(neighborsPQ.remove().getElement());
//        }
//      }
      for (KDTreeNode<T> n : neighborsList) {
        neighborReturnList.add(n.getElement());
      }
    }
//    Collections.reverse(neighborReturnList);
    return neighborReturnList;
  }

  /**
   * Recursive call for neighbors method.
   *
   * @param k      an integer putting a restraint on the neighbors search.
   * @param curr   the current node we're comparing to in our search.
   * @param target the target point to look around.
   */
  public void neighborsSearch(int k, KDTreeNode<T> curr, KDTreeNode<T> target) {
    //check if curr is within radius
    //get the euclidean distance from target to curr
    double distanceFromTarget = this.euclideanDistance(curr, target);
    distanceMap.put(curr, distanceFromTarget); //store distance
    //If the current node is closer to your target point than one of your k-nearest neighbors,
    // or if your collection of neighbors is not full, update the list accordingly
    if ((neighborsPQ.size() < k + 1)) {
      //add to the queue, but dont add the target.
      neighborsPQ.add(curr);
    } else if (!(neighborsPQ.isEmpty())
        && distanceFromTarget < this.euclideanDistance(neighborsPQ.peek(), target)) {
      neighborsPQ.remove(); //pop head
      neighborsPQ.add(curr); //replace farthest neighbor with curr
    } else if (!(neighborsPQ.isEmpty())
        && distanceFromTarget == this.euclideanDistance(neighborsPQ.peek(), target)) {
      //randomize case
      Random r = new Random();
      if (r.nextInt(2) == 1) { //gives 1 or 0 randomly
        neighborsPQ.remove(); //pop head
        neighborsPQ.add(curr); //replace farthest neighbor with curr
      }
    }
    //Find the relevant axis (x, y, z) according to the depth
    Double currAxisValue = curr.getElement().getCoordinate(curr.getDepth() % numDimensions);
    Double targetAxisValue = target.getElement().getCoordinate(target.getDepth() % numDimensions);
//    Double axisDistance = Math.abs(targetAxisValue - currAxisValue);
    Double axisDistance = Math.abs(currAxisValue - targetAxisValue);
    //If the euclidean distance between the target point and the farthest
    // neighbor you have is greater than the relevant axis distance*
    // between the current node and target point
    if (!(neighborsPQ.isEmpty())
        && this.euclideanDistance(neighborsPQ.peek(), target) >= axisDistance) {
      //recur on both children.
      if (!(curr.getLeftChild() == null)) {
        this.neighborsSearch(k, curr.getLeftChild(), target);
      }
      if (!(curr.getRightChild() == null)) {
        this.neighborsSearch(k, curr.getRightChild(), target);
      }
    } else {
      //If the current node's coordinate on the relevant axis is less than target's coordinate
      // on the relevant axis, recur on the right child.
      if (curr.getElement().getCoordinate(curr.getDepth() % numDimensions)
          < target.getElement().getCoordinate(curr.getDepth() % numDimensions)) {
        //recur on right child
        if (!(curr.getRightChild() == null)) {
          this.neighborsSearch(k, curr.getRightChild(), target);
        }
        //Else if the current node's coordinate on the relevant axis is greater than the target's
        //coordinate on the relevant axis, recur on the left child.
      } else if (curr.getElement().getCoordinate(curr.getDepth() % numDimensions)
          >= target.getElement().getCoordinate(curr.getDepth() % numDimensions)) {
        if (!(curr.getLeftChild() == null)) {
          this.neighborsSearch(k, curr.getLeftChild(), target);
        }
      }
    }
  }

  //(int k, KDTreeNode<T> curr, KDTreeNode<T> target)
  private void searchNeighbors(int numNeighbors, KDTreeNode<T> curr, KDTreeNode<T> target,
                               ArrayList<KDTreeNode<T>> nearestNeighbors) {
    if (curr == null) {
      return;
    }
    //if list is not at capacity, add
    if (nearestNeighbors.size() < numNeighbors) {
      nearestNeighbors.add(curr);
    } else if (euclideanDistance(curr, target) < euclideanDistance(target,
        nearestNeighbors.get(nearestNeighbors.size() - 1))) {
      //switch for closer neighbor
      nearestNeighbors.remove(nearestNeighbors.size() - 1);
      nearestNeighbors.add(curr);
      //if the distance to neighbor is equal, randomize which neighbor to keep in return list.
    } else if (euclideanDistance(curr, target) == euclideanDistance(target,
        nearestNeighbors.get(nearestNeighbors.size() - 1))) {
      if (Math.random() < RANDOM_CHANCE) {
        nearestNeighbors.remove(nearestNeighbors.size() - 1);
        nearestNeighbors.add(curr);
      }
    }
    Collections.sort(nearestNeighbors, new NodeDistanceComparator(target));
    //set up variable for recursion conditions
    double maxDistance =
        euclideanDistance(target, nearestNeighbors.get(nearestNeighbors.size() - 1));
    int coord = curr.getDepth() % numDimensions;
    if (maxDistance >= Math.abs(
        target.getElement().getCoordinate(coord) - curr.getElement().getCoordinate(coord))) {
      searchNeighbors(numNeighbors, curr.getLeftChild(), target, nearestNeighbors);
      searchNeighbors(numNeighbors, curr.getRightChild(), target, nearestNeighbors);
    } else if (curr.getElement().getCoordinate(coord) < target.getElement().getCoordinate(coord)) {
      searchNeighbors(numNeighbors, curr.getRightChild(), target, nearestNeighbors);
    } else if (curr.getElement().getCoordinate(coord) > target.getElement().getCoordinate(coord)) {
      searchNeighbors(numNeighbors, curr.getLeftChild(), target, nearestNeighbors);
    } else {
      //terminate the recursive call
      return;
    }
  }
  //*********
//  public void neighborsSearch(int k, KDTreeNode<T> curr, KDTreeNode<T> target) {
//    //check if curr is within radius
//    //get the euclidean distance from target to curr
//    double distanceFromTarget = this.euclideanDistance(curr, target);
//    distanceMap.put(curr, distanceFromTarget); //store distance
//    //If the current node is closer to your target point than one of your k-nearest neighbors,
//    // or if your collection of neighbors is not full, update the list accordingly
//    if ((neighborsPQ.size() < k + 1)) {
//      //add to the queue, but dont add the target.
//      neighborsPQ.add(curr);
//    } else if (!(neighborsPQ.isEmpty())
//        && distanceFromTarget < this.euclideanDistance(neighborsPQ.peek(), target)) {
//      neighborsPQ.remove(); //pop head
//      neighborsPQ.add(curr); //replace farthest neighbor with curr
//    } else if (!(neighborsPQ.isEmpty())
//        && distanceFromTarget == this.euclideanDistance(neighborsPQ.peek(), target)) {
//      //randomize case
//      Random r = new Random();
//      if (r.nextInt(2) == 1) { //gives 1 or 0 randomly
//        neighborsPQ.remove(); //pop head
//        neighborsPQ.add(curr); //replace farthest neighbor with curr
//      }
//    }
//    //Find the relevant axis (x, y, z) according to the depth
//    Double currAxisValue = curr.getElement().getCoordinate(curr.getDepth() % numDimensions);
//    Double targetAxisValue = target.getElement().getCoordinate(target.getDepth() % numDimensions);
////    Double axisDistance = Math.abs(targetAxisValue - currAxisValue);
//    Double axisDistance = Math.abs(currAxisValue - targetAxisValue);
//    //If the euclidean distance between the target point and the farthest
//    // neighbor you have is greater than the relevant axis distance*
//    // between the current node and target point
//    if (!(neighborsPQ.isEmpty())
//        && this.euclideanDistance(neighborsPQ.peek(), target) >= axisDistance) {
//      //recur on both children.
//      if (!(curr.getLeftChild() == null)) {
//        this.neighborsSearch(k, curr.getLeftChild(), target);
//      }
//      if (!(curr.getRightChild() == null)) {
//        this.neighborsSearch(k, curr.getRightChild(), target);
//      }
//    } else {
//      //If the current node's coordinate on the relevant axis is less than target's coordinate
//      // on the relevant axis, recur on the right child.
//      if (curr.getElement().getCoordinate(curr.getDepth() % numDimensions)
//          < target.getElement().getCoordinate(curr.getDepth() % numDimensions)) {
//        //recur on right child
//        if (!(curr.getRightChild() == null)) {
//          this.neighborsSearch(k, curr.getRightChild(), target);
//        }
//        //Else if the current node's coordinate on the relevant axis is greater than the target's
//        //coordinate on the relevant axis, recur on the left child.
//      } else if (curr.getElement().getCoordinate(curr.getDepth() % numDimensions)
//          >= target.getElement().getCoordinate(curr.getDepth() % numDimensions)) {
//        if (!(curr.getLeftChild() == null)) {
//          this.neighborsSearch(k, curr.getLeftChild(), target);
//        }
//      }
//    }
//  }

  /**
   * Helper method calculating the euclidean distance between two nodes.
   *
   * @param a KDTreeNode.
   * @param b a second KDTreeNode.
   * @return double
   */
  public Double euclideanDistance(KDTreeNode<T> a, KDTreeNode<T> b) {
    double total = 0;
    for (int i = 0; i < numDimensions; i++) {
      total = total
          + Math.pow(a.getElement().getCoordinate(i) - b.getElement().getCoordinate(i), 2);
    }
    return Math.sqrt(total);
  }

  /**
   * Getter method for root.
   *
   * @return the root of the tree
   */
  public KDTreeNode<T> getRoot() {
    return root;
  }


  /**
   * Comparator used for sorting nodes according to their
   * distance to a particular set of coordinates.
   */
  private class NodeDistanceComparator implements Comparator<KDTreeNode<T>> {
    private KDTreeNode<T> tar;

    /**
     * Constructor for comparator, takes in basis coordinates.
     *
     * @param target Target node.
     */
    NodeDistanceComparator(KDTreeNode<T> target) {
      tar = target;
    }

    /**
     * Function for comparing two nodes, on basis of their
     * distance from a set of nodes.
     *
     * @param o1 a tree node
     * @param o2 another tree node
     * @return a numeric value indicating greatr than or less than
     */
    @Override
    public int compare(KDTreeNode o1, KDTreeNode o2) {
      if (euclideanDistance(o1, tar) < euclideanDistance(o2, tar)) {
        return -1;
      } else if (euclideanDistance(o1, tar) > euclideanDistance(o2, tar)) {
        return 1;
      } else {
        return 0;
      }
    }
  }

}