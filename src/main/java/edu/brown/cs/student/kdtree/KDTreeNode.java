package edu.brown.cs.student.kdtree;

import edu.brown.cs.student.stars.HasCoordinates;

/**
 * Class modeling a kdtree node stores an element of type HasCoordinates.
 *
 * @param <T>
 * Takes in a Generic T that extends the HasCoordinates interface.
 */
public class KDTreeNode<T extends HasCoordinates> {
  private KDTreeNode<T> leftChild;
  private KDTreeNode<T> rightChild;
  private final T element;
  private final int d;

  /**
   * Constructor.
   *
   * @param elem
   *      contains information about the node
   * @param left
   *      the left child of the node
   * @param right
   *      the right child of the node
   * @param depth
   *      the depth of the node in the KDTree
   */
  public KDTreeNode(T elem, KDTreeNode<T> left, KDTreeNode<T> right, int depth) {
    this.element = elem;
    this.leftChild = left;
    this.rightChild = right;
    d = depth;
  }

  /**
   * Gets the left child of a given node.
   *
   * @return
   *      left child of node
   */
  public KDTreeNode<T> getLeftChild() {
    return leftChild;
  }

  /**
   * Sets the left child of a given node.
   *
   * @param child
   *      setting the left child
   */
  public void setLeftChild(KDTreeNode<T> child) {
    leftChild = child;
  }

  /**
   * Gets the right child of a given node.
   *
   * @return
   *      right child of node
   */
  public KDTreeNode<T> getRightChild() {
    return rightChild;
  }

  /**
   * Sets the right child of a given node.
   *
   * @param child
   *      setting the right child
   */
  public void setRightChild(KDTreeNode<T> child) {
    rightChild = child;
  }

  /**
   * Gets the element of the node, an object that extends HasCoordinates.
   *
   * @return
   *      T HasCoordinates element
   */
  public T getElement() {
    return element;
  }

  /**
   * gets the depth of the given node in the tree.
   *
   * @return
   *      int
   */
  public int getDepth() {
    return d;
  }
}
