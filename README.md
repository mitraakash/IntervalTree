# IntervalTree

The interval tree is a special type of binary search tree, where each node contains a number, called the split value. Also, each node, except for the leaf nodes (square nodes), stores two lists of intervals, each interval containing the split value. Both lists contain the same intervals, but one list is sorted by the left endpoints, and the other by the right endpoints.
The leaf nodes are built out of the left and right endpoints of the intervals, arranged in sorted order, without duplicates. That is, if an endpoint occurs in more than one interval, only one copy of the endpoint is used. In the example, the unique endpoints are 1,2,3,5,6,7,8 - each of these will result in a leaf node. For each leaf node, the split value is the endpoint itself. Remember, the leaf nodes do not have any intervals.

For every internal (i.e. non-leaf) node, its split value is the midpoint of the largest value in its left subtree and the smallest value in its right subtree. These values will be found at the appropriate leaf nodes in the respective subtrees. (Note: The split value of a node is NOT the midpoint of the split values of its children.)

Every interval from S appears in only one node of the tree, and this node is the "highest" node (closest to the root) in the tree whose split value falls within the interval. In other words, each interval from S is placed in the tree as high as it can legally go, given the rule that it must be placed in a node whose split value is contained in the interval.
