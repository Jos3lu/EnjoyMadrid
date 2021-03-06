/*  This file is part of Openrouteservice.
 *
 *  Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the 
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 
 *  of the License, or (at your option) any later version.

 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License along with this library; 
 *  if not, see <https://www.gnu.org/licenses/>.  
 */
package org.heigit.ors.matrix.algorithms;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;

import org.heigit.ors.matrix.MatrixLocations;
import org.heigit.ors.matrix.MatrixRequest;
import org.heigit.ors.matrix.MatrixResult;

public interface MatrixAlgorithm {
  void init(MatrixRequest req, GraphHopper gh, Graph graph, FlagEncoder encoder, Weighting weighting);
  
  MatrixResult compute(MatrixLocations srcData, MatrixLocations dstData, int metrics) throws Exception;
}
