/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xmldrill;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;

import com.Ostermiller.util.CSVParser;

/**
 * @author Ilya Boyandin
 */
public class XmlDrillApplication extends WebApplication
{

    private final Rankings rankings;
    
	public XmlDrillApplication()
	{
	    rankings = new Rankings();
	}
	
	@Override
	protected void init() {
	    super.init();
	    
	    File rf = new File(System.getProperty("catalina.home") + "/tm-ranking.txt");
	    if (rf.exists()) {
	        try {
                String[][] lines =
                    CSVParser.parse(new InputStreamReader(new FileInputStream(rf), "utf-8"), '\t', "nrtf", "\n\r\t\f", "#!");
                for (String[] line : lines) {
                    rankings.addPoints(line[0], line[1], line[2], Integer.parseInt(line[3]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
	    }
	}
	
	public Rankings getRankings() {
        return rankings;
    }

	@Override
    public Session newSession(Request request, Response response) {
        return new MySession(request);
    }

	@Override
	public RequestCycle newRequestCycle(Request request, Response response)
	{
		return new WebRequestCycle(this, (WebRequest)request, response)
		{
			@Override
			protected void onBeginRequest()
			{
				// open session
			}

			@Override
			protected void onEndRequest()
			{
				// close session
			}
		};
	}

	@Override
	public Class<HomePage> getHomePage()
	{
		return HomePage.class;
	}
}
