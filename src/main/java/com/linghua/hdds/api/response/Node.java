package com.linghua.hdds.api.response;

import java.util.ArrayList;
import java.util.List;

public class Node {

        private String pId;
        private String caId;
        private String caName;
        private List<Node> children = new ArrayList<>();

        public Node(String pId, String caId, String caName) {
            super();
            this.pId = pId;
            this.caId = caId;
            this.caName = caName;

        }

    public Node() {
    }

    public String getpId() {
            return pId;
        }

        public void setpId(String pId) {
            this.pId = pId;
        }

        public String getCaId() {
            return caId;
        }

        public void setCaId(String caId) {
            this.caId = caId;
        }

        public String getCaName() {
            return caName;
        }

        public void setCaName(String caName) {
            this.caName = caName;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setChildren(List<Node> children) {
            this.children = children;
        }

        public void addChildren(Node child) {
            if (children == null)
                children = new ArrayList<>();
            children.add(child);
        }

        public boolean hasChldren() {
            return children != null && children.size() > 0;
        }

        public String toString() {

            StringBuffer buff = new StringBuffer();
            buff.append("[  ").append(this.caName);

            if (hasChldren()) {

                for (Node node : getChildren()) {
                    buff.append("\n").append("\t").append(">>>>");
                    buff.append(node.toString());
                }
                buff.append(" ]\n  ");
            }
            buff.append(" ]\n  ");

            return buff.toString();
            // return this.pId+"::"+this.caName+":"+
            // (hasChldren()?getChildren().size():0);
        }

}

