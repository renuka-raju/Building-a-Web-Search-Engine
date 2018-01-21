import networkx as nx
from networkx import pagerank

def graphpagerank():
    openfile=open('F:/BG/BG/output.txt', 'w')
    G = nx.read_edgelist("F:/BG/BG/EdgeList.txt", create_using=nx.DiGraph());
    x=pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',dangling=None);
    c=0
    print(x)
    print(type(x))
    for k,v in x.items():
        openfile.write(k+'='+str(v)+'\n')
        #print(k+'='+str(v))
        c+=1
        #if(c==5):
            #break
    #with open('F:/BG/BG/output.txt', 'w') as outFile:
     #   outFile.write(x)

graphpagerank()
