#  Copyright (c) 2014 "Kaazing Corporation," (www.kaazing.com)
#
#  This file is part of Robot.
#
#  Robot is free software: you can redistribute it and/or modify
#  it under the terms of the GNU Affero General Public License as
#  published by the Free Software Foundation, either version 3 of the
#  License, or (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Affero General Public License for more details.
#
#  You should have received a copy of the GNU Affero General Public License
#  along with this program. If not, see <http://www.gnu.org/licenses/>.

#!/bin/bash
mkdir ./tmp

# Start Robot
nohup java -jar ../robot.jar start background &> /dev/null &
sleep 5
echo Robot Started

# Store Robot pid
echo $! >> ./tmp/robot_pid

# Build tests and execute them
make
./example-tests

# Kill Robot process
kill -9 $(cat ./tmp/robot_pid)

echo Robot Stopped

# Clean-up
rm -r ./tmp
