def toClassText(input):
    className, *spl = input.split("|")
    initFunc = "    public void init() {\n      " + ";\n      ".join(spl) + ";\n    }"
    declaration = "    public float " + ";\n    public float ".join(spl) + ";"
    whole = "public final class Stats {\n" + declaration + "\n\n" + initFunc + "\n\n    public Stats() {init();}\n  } // end of generated stats"
    return whole


def replaceStats(input, newStats):
    found = input.find("public final class Stats {")
    if found == -1:
        return None
    found2 = input.find("// end of generated stats")
    if found2 == -1:
        return None

    input = [*input]
    input[found:found2+len("// end of generated stats")] = newStats
    input="".join(input)
    print(input)

def main():
    s=toClassText(
        "Player|speed=1|health=100|cd=5|projSize=10|projSpeed=30|projPierce=100|projDuration=3|projPower=100")
    with open("testJavaFile.txt", "r") as file:
        text=file.read()
        print(text)
    text=replaceStats(text, s)
    with open("testJavaFile.txt", "w") as file:
        file.write(text)


main()
