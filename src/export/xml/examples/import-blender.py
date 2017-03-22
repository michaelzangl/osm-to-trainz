from xml.dom import minidom
import bpy

FILE="/home/michael/workspace/Trainz Map/src/test-export.xml"
OBJBASE="/home/michael/workspace/Trainz Map/src/"

def loadExportfile(file):
    document=minidom.parse(file)
    root=document.documentElement
    
    for node in root.childNodes:
        if node.nodeType != root.ELEMENT_NODE:
            continue
        if node.tagName == "object":
            loadExportObject(node)
    
def loadExportObject(node):
    if not node.hasAttribute("x") or not node.hasAttribute("y") or not node.hasAttribute("z"):
        return
    
    tags={}
    for child in node.childNodes:
        if child.nodeType != node.ELEMENT_NODE:
            continue
        if child.tagName == "tag":
            tags[child.getAttribute("k")] = child.getAttribute("v")
    
    obj=ObjectLoader(node.getAttribute("name"), tags)
    
    if obj.mesh is None:
        print("No mesh found in object " + node.getAttribute("name"))
        return
    
    x=int(node.getAttribute("x"))
    y=int(node.getAttribute("y"))
    z=int(node.getAttribute("height"))
    
    scene = bpy.context.scene
    for o in scene.objects:
        o.select = False
    bobject = bpy.data.objects.new(node.getAttribute("name"), obj.mesh)
    bobject.location = (x,y,z)
    scene.objects.link(bobject)

    
class ObjectLoader:
    parameters = {}
    #todo: mesh
    mesh = None
    
    def __init__(self, name, tags):
        self.tags = tags
        self.loadFile(name)

    def loadFile(self, name):
        file = OBJBASE + name + ".xml"
        document = minidom.parse(file)
        root = document.documentElement
        
        for node in root.childNodes:
            if node.nodeType != root.ELEMENT_NODE:
                continue
            if node.tagName == "parameter-script":
                self.executeParameterScript(node)
            elif node.tagName == "mesh":
                self.loadMesh(node)
                
    
    def executeParameterScript(self, node):
        name=node.getAttribute("name")
        value=""
        for child in node.childNodes:
            if child.nodeType != node.ELEMENT_NODE:
                continue
            if child.tagName == "load-tag":
                value = self.executeLoadTag(child)
            elif child.tagName == "interprete":
                value = self.executeInterprete(child, value)
            elif child.tagName == "round":
                value = self.executeRound(child, value)
        self.parameters[name] = value
        
    def executeLoadTag(self, node):
        k=node.getAttribute("tag")
        if k in self.tags:
            return self.tags[k]
        else:
            return node.getAttribute("default")
    
    def executeRound(self, node, value):
        return value
    
    def executeInterprete(self, node, value):
        return value
    
    """ Load the mesh """
    def loadMesh(self, node):
        triangles = []
        for child in node.childNodes:
            if child.nodeType != node.ELEMENT_NODE:
                continue
            if child.tagName == "triangle":
                tri = self.convertTriangle(child)
                if tri is not None:
                    triangles.append(tri)
         
        #vertex -> index
        vertexmap  = {}
        vertexlist = []
        faces = []
        
        for triangle in triangles:
            vertindexes = []
            for vertex in triangle:
                print(vertex)
                position = (vertex["x"],vertex["y"],vertex["z"])
                vertindex = vertexmap.get(position)
                if vertindex is None:
                    vertindex = len(vertexlist)
                    vertexlist.append(position)
                    vertexmap[position] = vertindex
                vertindexes.append(vertindex)
            faces.append(vertindexes)
        
        self.mesh = bpy.data.meshes.new("import")
        self.mesh.from_pydata(vertexlist, [], faces)
        self.mesh.update()
        self.mesh.validate()
    
    
    def convertTriangle(self, node):
        verts = []
        for child in node.childNodes:
            if child.nodeType != node.ELEMENT_NODE:
                continue
            if child.tagName == "vertex":
                vert = {}
                for arg in ("x","y","z","u","v"):
                    if child.hasAttribute(arg):
                        vert[arg] = self.evaluateNumber(child.getAttribute(arg))
                    else:
                        vert[arg] = 0.0
                verts.append(vert)
        
        if len(verts) == 3:
            return verts
        else:
            return None
        
    def evaluateNumber(self, stringOrNumber):
        try:
            value = float(stringOrNumber)
        except ValueError:
            value = self.parameters.get(stringOrNumber)
            if value is None:
                raise ValueError("'" + str(stringOrNumber) + "'is neither a value nor a parameter")
            value = float(value)
        return value
    
    
loadExportfile(FILE)
