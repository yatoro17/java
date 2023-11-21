// AVLSearchTree.java
package simpletexteditor;

class AVLSearchTree {
    private AVLNode root;

    public AVLSearchTree() {
        this.root = null;
    }

    public void insert(String word) {
        root = insert(root, word.toLowerCase());
    }

    private AVLNode insert(AVLNode node, String word) {
        if (node == null) {
            return new AVLNode(word);
        }

        int compareResult = word.compareTo(node.data);
        if (compareResult < 0) {
            node.left = insert(node.left, word);
        } else if (compareResult > 0) {
            node.right = insert(node.right, word);
        } else {
            // Duplicate word, do nothing
            return node;
        }

        // Update height
        node.height = 1 + Math.max(height(node.left), height(node.right));

        // Perform balance check and rotations
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && word.compareTo(node.left.data) < 0) {
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && word.compareTo(node.right.data) > 0) {
            return rotateLeft(node);
        }

        // Left Right Case
        if (balance > 1 && word.compareTo(node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right Left Case
        if (balance < -1 && word.compareTo(node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    public boolean search(String word) {
        return search(root, word.toLowerCase());
    }

    private boolean search(AVLNode node, String word) {
        while (node != null) {
            int compareResult = word.compareTo(node.data);
            if (compareResult < 0) {
                node = node.left;
            } else if (compareResult > 0) {
                node = node.right;
            } else {
                return true; // Match found
            }
        }
        return false; // Match not found
    }

    public int countWords() {
        return countWords(root);
    }

    private int countWords(AVLNode node) {
        if (node == null) {
            return 0;
        }
        int leftCount = countWords(node.left);
        int rightCount = countWords(node.right);
        return leftCount + rightCount + 1;
    }

    private int height(AVLNode node) {
        return (node != null) ? node.height : 0;
    }

    private int getBalance(AVLNode node) {
        return (node != null) ? height(node.left) - height(node.right) : 0;
    }

    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private class AVLNode {
        String data;
        AVLNode left;
        AVLNode right;
        int height;

        public AVLNode(String data) {
            this.data = data;
            this.height = 1;
        }
    }
}
