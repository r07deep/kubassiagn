# kubassiagn

This is the initial implementation :
1.	Capturing the http://localhost:9090/api/node/groups request of user in PodController.java.
2.	PodController calls the ProcessPodsInfo.java, which reads the data from filesystem. NOTE: Having issue with http://localhost:8080/v1/api/pods in code ,so read from filesystem.
3.	Then further processed the RAW data with MapReduce.java and generated the following. 
    Output of MapReduce.java Intermediate data-> Node distribution with AppGroups 
    {n1=[a, b, c], n5=[a, b, c], n3=[d, e, f, g], n2=[d, e, g], n6=[d, e, f], n4=[f, g]}
4.	Now further used the above distribution list for form the final group mapping
    based on the check for the matching apps across all the nodes. 
    Final Output Achived: 
    g1: {
    nodes: [n1, n5],
    app: [a, b, c]
    },
    g2: {
    nodes: [ n2, n3, n4, n6 ],
    app: [d, e, f, g]
    }
    }
