@echo off
cd assets

echo ------ > ..\build_log.tmp
echo assets images: >> ..\build_log.tmp

python main.py >> ..\build_log.tmp
cd ..
echo ------ >> build_log.tmp
echo HardcodeStats.py >> build_log.tmp
python HardcodeStats.py >> build_log.tmp
type build_log.tmp > build_log.txt
del build_log.tmp
PAUSE