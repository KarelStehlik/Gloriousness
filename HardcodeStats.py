def toClassText(input):
    className, *spl = input.split("|")
    initFunc = "        public void init() {\n            " + ";\n            ".join(spl)+";\n        }"
    declaration = "        public float " + ";\n        public float ".join(spl) + ";"
    whole = "    public final class Stats {\n" + declaration + "\n\n" + initFunc + "\n\n        public Stats() {init();}\n    }"
    return whole

def main():
    print(toClassText(
        "Player|speed=1|health=100|cd=5|projSize=10|projSpeed=30|projPierce=100|projDuration=3|projPower=100"))


main()
