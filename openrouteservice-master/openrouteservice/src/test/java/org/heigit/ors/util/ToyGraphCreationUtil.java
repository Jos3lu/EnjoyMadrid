package org.heigit.ors.util;

import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphBuilder;
import com.graphhopper.storage.GraphHopperStorage;

public class ToyGraphCreationUtil {
    private static GraphHopperStorage createGHStorage(EncodingManager encodingManager) {
        return new GraphBuilder(encodingManager).create();
    }

    public static GraphHopperStorage createMediumGraph(EncodingManager encodingManager) {
        //    3---4--5
        //   /\   |  |
        //  2--0  6--7
        //  | / \   /
        //  |/   \ /
        //  1-----8
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 3, 5, true);
        g.edge(0, 8, 1, true);
        g.edge(1, 2, 1, true);
        g.edge(1, 8, 2, true);
        g.edge(2, 3, 2, true);
        g.edge(3, 4, 2, true);
        g.edge(4, 5, 1, true);
        g.edge(4, 6, 1, true);
        g.edge(5, 7, 1, true);
        g.edge(6, 7, 2, true);
        g.edge(7, 8, 3, true);
        //Set test lat lon
        g.getBaseGraph().getNodeAccess().setNode(0, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(1, 1, 1);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 1);
        g.getBaseGraph().getNodeAccess().setNode(3, 4, 2);
        g.getBaseGraph().getNodeAccess().setNode(4, 4, 4);
        g.getBaseGraph().getNodeAccess().setNode(5, 4, 5);
        g.getBaseGraph().getNodeAccess().setNode(6, 3, 4);
        g.getBaseGraph().getNodeAccess().setNode(7, 3, 5);
        g.getBaseGraph().getNodeAccess().setNode(8, 1, 4);
        return g;
    }

    public static GraphHopperStorage createMediumGraph(GraphHopperStorage g) {
        //    3---4--5
        //   /\   |  |
        //  2--0  6--7
        //  | / \   /
        //  |/   \ /
        //  1-----8
        g.edge(0, 1, 1, true); //0
        g.edge(0, 2, 1, true); //1
        g.edge(0, 3, 5, true); //2
        g.edge(0, 8, 1, true); //3
        g.edge(1, 2, 1, true); //4
        g.edge(1, 8, 2, true); //5
        g.edge(2, 3, 2, true); //6
        g.edge(3, 4, 2, true); //7
        g.edge(4, 5, 1, true); //8
        g.edge(4, 6, 1, true); //9
        g.edge(5, 7, 1, true); //10
        g.edge(6, 7, 2, true); //11
        g.edge(7, 8, 3, true); //12
        //Set test lat lon
        g.getBaseGraph().getNodeAccess().setNode(0, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(1, 1, 1);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 1);
        g.getBaseGraph().getNodeAccess().setNode(3, 4, 2);
        g.getBaseGraph().getNodeAccess().setNode(4, 4, 4);
        g.getBaseGraph().getNodeAccess().setNode(5, 4, 5);
        g.getBaseGraph().getNodeAccess().setNode(6, 3, 4);
        g.getBaseGraph().getNodeAccess().setNode(7, 3, 5);
        g.getBaseGraph().getNodeAccess().setNode(8, 1, 4);
        return g;
    }

    public static GraphHopperStorage createMediumGraph2(EncodingManager encodingManager) {
        //    3---4--5
        //   /\   |  |
        //  2--0  6--7
        //  | / \   /
        //  |/   \ /
        //  1-----8
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 3, 5, true);
        g.edge(0, 8, 1, true);
        g.edge(1, 2, 1, true);
        g.edge(1, 8, 2, true);
        g.edge(2, 3, 2, true);
        g.edge(3, 4, 2, true);
        g.edge(4, 5, 1, true);
        g.edge(4, 6, 1, true);
        g.edge(5, 7, 1, true);
        g.edge(6, 7, 2, true);
        g.edge(7, 8, 3, true);
        //Set test lat lon
        g.getBaseGraph().getNodeAccess().setNode(0, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(1, 1, 1);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 1);
        g.getBaseGraph().getNodeAccess().setNode(3, 4, 2);
        g.getBaseGraph().getNodeAccess().setNode(4, 4, 5);
        g.getBaseGraph().getNodeAccess().setNode(5, 4, 6);
        g.getBaseGraph().getNodeAccess().setNode(6, 3, 5);
        g.getBaseGraph().getNodeAccess().setNode(7, 3, 6);
        g.getBaseGraph().getNodeAccess().setNode(8, 1, 4);
        return g;
    }

    public static GraphHopperStorage createMediumGraphWithAdditionalEdge(EncodingManager encodingManager) {
        //    3---4--5--9
        //   /\   |  |
        //  2--0  6--7
        //  | / \   /
        //  |/   \ /
        //  1-----8
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 3, 5, true);
        g.edge(0, 8, 1, true);
        g.edge(1, 2, 1, true);
        g.edge(1, 8, 2, true);
        g.edge(2, 3, 2, true);
        g.edge(3, 4, 2, true);
        g.edge(4, 5, 1, true);
        g.edge(4, 6, 1, true);
        g.edge(5, 7, 1, true);
        g.edge(5, 9, 1, true);
        g.edge(6, 7, 2, true);
        g.edge(7, 8, 3, true);
        //Set test lat lon
        g.getBaseGraph().getNodeAccess().setNode(0, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(1, 1, 1);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 1);
        g.getBaseGraph().getNodeAccess().setNode(3, 4, 2);
        g.getBaseGraph().getNodeAccess().setNode(4, 4, 4);
        g.getBaseGraph().getNodeAccess().setNode(5, 4, 5);
        g.getBaseGraph().getNodeAccess().setNode(6, 3, 4);
        g.getBaseGraph().getNodeAccess().setNode(7, 3, 5);
        g.getBaseGraph().getNodeAccess().setNode(8, 1, 4);
        g.getBaseGraph().getNodeAccess().setNode(9, 4, 6);
        return g;
    }

    public static GraphHopperStorage createSingleEdgeGraph(EncodingManager encodingManager) {
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);

        g.getBaseGraph().getNodeAccess().setNode(0, 0, 0);
        g.getBaseGraph().getNodeAccess().setNode(1, 1, 1);

        return g;
    }

    public static GraphHopperStorage createSimpleGraph(EncodingManager encodingManager) {
        // 5--1---2
        //     \ /|
        //      0 |
        //     /  |
        //    4---3
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 4, 3, true);
        g.edge(1, 2, 2, true);
        g.edge(2, 3, 1, true);
        g.edge(4, 3, 2, true);
        g.edge(5, 1, 2, true);

        g.getBaseGraph().getNodeAccess().setNode(0, 2, 2);
        g.getBaseGraph().getNodeAccess().setNode(1, 3, 2);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(3, 1, 3);
        g.getBaseGraph().getNodeAccess().setNode(4, 1, 2);
        g.getBaseGraph().getNodeAccess().setNode(5, 3, 1);
        return g;
    }

    public static GraphHopperStorage createSimpleGraph2(EncodingManager encodingManager) {
        // 5--1---2
        //     \ /
        //      0
        //     /
        //    4--6--3
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 4, 3, true);
        g.edge(1, 2, 2, true);
        g.edge(4, 6, 2, true);
        g.edge(6, 3, 2, true);
        g.edge(5, 1, 2, true);

        g.getBaseGraph().getNodeAccess().setNode(0, 2, 2);
        g.getBaseGraph().getNodeAccess().setNode(1, 3, 2);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(3, 1, 4);
        g.getBaseGraph().getNodeAccess().setNode(4, 1, 2);
        g.getBaseGraph().getNodeAccess().setNode(5, 3, 1);
        g.getBaseGraph().getNodeAccess().setNode(6, 3, 3);
        return g;
    }

    public static GraphHopperStorage createSimpleGraphWithoutLatLon(EncodingManager encodingManager) {
        // 5--1---2
        //     \ /|
        //      0 |
        //     /  |
        //    4---3
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 4, 3, true);
        g.edge(1, 2, 2, true);
        g.edge(2, 3, 1, true);
        g.edge(4, 3, 2, true);
        g.edge(5, 1, 2, true);

        return g;
    }

    public static GraphHopperStorage createDisconnectedGraph(EncodingManager encodingManager) {
        //   5--1---2
        //       \ /
        //        0
        //       /
        //      /
        //     / 6  9
        //    /  |  |
        //   /   7--8
        //  4---3
        //  |   |
        //  11  10
        GraphHopperStorage g = createGHStorage(encodingManager);
        g.edge(0, 1, 1, true);
        g.edge(0, 2, 1, true);
        g.edge(0, 4, 3, true);
        g.edge(1, 2, 2, true);
        g.edge(4, 3, 2, true);
        g.edge(5, 1, 2, true);
        g.edge(6, 7, 1, true);
        g.edge(7, 8, 1, true);
        g.edge(8, 9, 1, true);
        g.edge(3, 10, 1, true);
        g.edge(4, 11, 1, true);

        g.getBaseGraph().getNodeAccess().setNode(0, 2, 2);
        g.getBaseGraph().getNodeAccess().setNode(1, 3, 2);
        g.getBaseGraph().getNodeAccess().setNode(2, 3, 3);
        g.getBaseGraph().getNodeAccess().setNode(3, 1, 3);
        g.getBaseGraph().getNodeAccess().setNode(4, 1, 2);
        g.getBaseGraph().getNodeAccess().setNode(5, 3, 1);
        g.getBaseGraph().getNodeAccess().setNode(6, 1.2, 3);
        g.getBaseGraph().getNodeAccess().setNode(7, 1.1, 3);
        g.getBaseGraph().getNodeAccess().setNode(8, 1.1, 2);
        g.getBaseGraph().getNodeAccess().setNode(9, 1.2, 2);
        g.getBaseGraph().getNodeAccess().setNode(10, 0.8, 2.2);
        g.getBaseGraph().getNodeAccess().setNode(11, 0.8, 2);

        return g;
    }

    public static GraphHopperStorage createDiamondGraph(GraphHopperStorage g) {
        //     4
        //   /   \
        //  2--0--3
        //   \   /
        //    \ /
        //     1
        g.edge(0, 2, 1, true); //0
        g.edge(0, 3, 3, true); //1
        g.edge(1, 2, 5, true); //2
        g.edge(1, 3, 3, true); //3
        g.edge(2, 4, 1, true); //4
        g.edge(3, 4, 1, true); //5
        return g;
    }

    public static GraphHopperStorage createUpDownGraph(GraphHopperStorage g) {
        //      8------9
        //       \    /
        //0---1---3  5---6---7
        //       / \/
        //      2  4
        g.edge(0, 1, 1, true); //0
        g.edge(1, 3, 1, true); //1
        g.edge(2, 3, 1, true); //2
        g.edge(3, 4, 1, true); //3
        g.edge(3, 8, 5, true); //4
        g.edge(4, 5, 1, true); //5
        g.edge(5, 6, 1, true); //6
        g.edge(5, 9, 5, true); //7
        g.edge(6, 7, 1, true); //8
        g.edge(8, 9, 1, true); //9
        return g;
    }

    public static GraphHopperStorage createTwoWayGraph(GraphHopperStorage g) {
        // 0<----------<-1
        // |             |
        // 2             |
        // | R           |
        // 3---4---5     |
        // |             |
        // 6-----7-------8
        // |
        // 9
        g.edge(0, 2, 1, false); //0
        g.edge(1, 0, 1, false); //1
        g.edge(2, 3, 1, false); //2
        g.edge(3, 4, 1, false); //3
        g.edge(4, 5, 1, true); //4
        g.edge(3, 6, 1, true); //5
        g.edge(7, 8, 1, true); //6
        g.edge(6, 9, 1, true); //7
        g.edge(6, 7, 10, true); //8
        g.edge(8, 1, 1, true); //9
        g.edge(8, 1, 1, true); //10 Just to put 8, 1 and 0 in core
        g.edge(1, 0, 1, false); //11  Just to put 8, 1 and 0 in core
        return g;
    }

    public static GraphHopperStorage createUpdatedGraph(GraphHopperStorage g) {
        //     2---3
        //    / \
        //   1  |
        //    \ |
        //     0
        g.edge(0, 1, 5, true); //0
        g.edge(0, 2, 1, true); //1
        g.edge(1, 2, 1, true); //2
        g.edge(2, 3, 1, true); //3

        return g;
    }

    public static GraphHopperStorage createDirectedGraph(GraphHopperStorage g) {
        // 0----->1<-----2
        // |     / \     |
        // |-<--/   \-->-|
        g.edge(0, 1, 1, false); //0
        g.edge(1, 0, 5, false); //1
        g.edge(1, 2, 6, false); //2
        g.edge(2, 1, 2, false); //3

        return g;
    }
}
