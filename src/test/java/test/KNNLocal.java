package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KNNLocal {

    public static void main(String[] args){

        Random r=new Random();
        int row=100;
        int col=6;
        double[][] data=new double[row][col];
        for(int i=0;i<row;i++){
            for (int j = 0; j < col; j++){
                data[i][j] = r.nextInt(10);
                System.out.print(data[i][j]+"  ");
            }
            System.out.println();
        }
        int[] index = sort(data,0);
        int pos = getPos(index);
        Node root = new Node(data[index[pos]]);
        root.setMid(root.getSelf()[0]);
        buildKDTree(root,data,index,pos,1);
        System.out.println(root.toString());

    }
    public static int[] sort(double[][] data,int colNum){
        int rowNum = data.length;
        int[] index =new int[data.length];
        double[] col=new double[data.length];
        for(int i=0;i<data.length;i++){
            col[i] = data[i][colNum];
            index[i] = i;
        }
        for(int i=0;i<rowNum;i++)
            for(int j=rowNum-1;j>i;j--){
              if (col[j]<col[j-1]){
                  double temp = col[j];
                  col[j]=col[j-1];
                  col[j-1]=temp;
                  int tmp2=index[j];
                  index[j] = index[j-1];
                  index[j-1] = tmp2;
              }
            }
        return index;

    }
    public static int getPos(int[] index){

        if(index.length<2){
          return index.length-1;
        }else return index.length/2;
    }

    public static void buildKDTree(Node node, double[][] data, int[] index, int pos, int col){
        int maxCol = data[0].length;
        if(col == maxCol-1){
            if(data.length!=0){
                List<Node> nodes=new ArrayList<>();
                for(double[] doubles:data){
                    nodes.add(new Node(doubles));
                }
                node.setLastLeft(nodes);
            }
            return ;
        }
        if(data.length==0)
            return;
        if(data.length == 1){
            List<Node> nodes=new ArrayList<>();
            for(double[] doubles:data){
                nodes.add(new Node(doubles));
            }
            node.setLastLeft(nodes);
            return;
        }
        double[][] leftRegin = new double[pos][];
        for(int start=0;start<pos;start++)
            leftRegin[start] = data[index[start]];
        int[] leftIndex = sort(leftRegin,col);
        int leftPos=getPos(leftIndex);
        Node left = new Node(leftRegin[leftIndex[leftPos]]);
        node.setLeft(left);
        left.setP(node);
        left.setMid(left.getSelf()[col]);
        buildKDTree(left,leftRegin,leftIndex,leftPos,col);
        if(pos <=1)
            return;
        double[][] rightRegin = new double[data.length-pos-1][];
        for(int i=1;i+pos<data.length;i++)
            rightRegin[i-1] = data[index[i+pos]];
        int[] rightIndex = sort(rightRegin,col );
        int rightPos=getPos(rightIndex);
        Node right = new Node(rightRegin[rightIndex[rightPos]]);
        node.setRight(right);
        right.setP(node);
        right.setMid(right.getSelf()[col]);
        buildKDTree(right,rightRegin,rightIndex,rightPos,col);

    }
}


class Node{
    private double[] self;
    private Node left;
    private Node right;
    private Node p;
    private List<Node> lastLeft;
    private double mid;


    public double[] getSelf() {
        return self;
    }

    public double getMid() {
        return mid;
    }

    public void setMid(double mid) {
        this.mid = mid;
    }

    public List<Node> getLastLeft() {
        return lastLeft;
    }

    public void setLastLeft(List<Node> lastLeft) {
        this.lastLeft = lastLeft;
    }

    public Node(double[] self) {
        this.self = self;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getP() {
        return p;
    }

    public void setP(Node p) {
        this.p = p;
    }

    @Override
    public String toString() {
        StringBuffer sb=new StringBuffer();
        sb.append(this.getMid());
        if(this.getLastLeft()!=null){
            sb.append("\n{").append(this.getLastLeft().size()).append("}");
        }
        if(this.getLeft()!=null){
            sb.append("l{").append(this.getLeft().toString()).append("}");
        }
        if(this.getRight()!=null){
            sb.append("\tr{").append(this.getRight().toString()).append("}");
        }
        return sb.toString();
    }
}