FIXED = true
VERILOGTB = false
VLSI_ROOT = ./build/vlsi/generated-src/

fpga: MEM=--inlineMem
fpga: clean_fpga setup_fpga vlsi
	cp ./build/vlsi/generated-src/*.v ./build/fpga/.
	cp generator_out.json ./build/fpga/.

asic: MEM=--noInlineMem
asic: clean_asic setup_asic vlsi
	cp ./build/vlsi/generated-src/*.v ./build/asic/.
	cp generator_out.json ./build/asic/.
	if [ -f ./build/vlsi/generated-src/$(PRJ).conf ]; then \
		cp ./build/vlsi/generated-src/$(PRJ).conf ./build/asic/. ;\
	fi

vlsi: clean_vlsi setup_vlsi $(VLSI_ROOT)
	sbt "run -params_true_false $(MEM) --genHarness --backend v --targetDir $(VLSI_ROOT)"
$(VLSI_ROOT):
	mkdir -p ./build/vlsi/generated-src


vlsi-asic: $(src_files)
	sbt "run -params_$(FIXED)_$(VERILOGTB) --genHarness --noInlineMem --debug --backend v --targetDir $(VLSI_ROOT)"
	if [ -a build/vlsi/generated-src/$(MODULE).conf ]; then$(mem_gen) build/vlsi/generated-src/$(MODULE).conf >>build/vlsi/generated-src/$(MODULE).v; fi
	g++ -c -o build/vlsi/generated-src/vpi.o -I$$VCS_HOME/include -Ibuild/vlsi/generated-src/ -fPIC -std=c++11 build/vlsi/generated-src/vpi.cpp


$(VLSI_ROOT):
	mkdir -p ./build/vlsi/generated-src


run-vlsi: $(src_files) vlsi-asic
	cd build/vlsi/vcs-sim-rtl && make
	cd ../../..
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --genHarness --backend null --debugMem --debug --targetDir build/vlsi/generated-src --testCommand \"build/vlsi/vcs-sim-rtl/CE\""



run-vlsi-vpd: $(src_files) vlsi-asic
	cd build/vlsi/vcs-sim-rtl && make
	cd ../../..
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --genHarness --backend null --debugMem --debug --targetDir build/vlsi/generated-src --testCommand \"build/vlsi/vcs-sim-rtl/CE\" +vcdfile=build/vlsi/vcs-sim-rtl/CE.vcd"


run-vlsi-syn: $(src_files) vlsi-asic
	cd build/vlsi/vcs-sim-gl-syn && make
	cd ../../..
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --backend null --targetDir build/vlsi/vcs-sim-gl-syn --testCommand \"build/vlsi/vcs-sim-gl-syn/CE -ucli -do build/vlsi/vcs-sim-gl-syn/+run_from_prjdir.tcl\""

run-vlsi-par: $(src_files) vlsi-asic
	cd build/vlsi/vcs-sim-gl-par && make SHELL=/bin/bash
	cd ../../..
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --backend null --targetDir build/vlsi/vcs-sim-gl-par --testCommand \"build/vlsi/vcs-sim-gl-par/CE -ucli -do build/vlsi/vcs-sim-gl-par/+run_from_prjdir.tcl\""



test: clean_test setup_test
	mkdir -p test/generated-src
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --debugMem --genHarness --compile --targetDir ./build/test" | tee console_out

debug: clean_test setup_test
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --debugMem --genHarness --compile --debug --targetDir ./build/test" | tee console_out

debug_vcd: clean_test setup_test
	sbt "run -params_$(FIXED)_$(VERILOGTB) --test --debugMem --genHarness --compile --debug --vcd --targetDir ./build/test" | tee console_out

setup_%:
	mkdir -p build/$(patsubst setup_%,%,$@)

clean_%:
	rm -rf build/$(patsubst clean_%,%,$@)

clean: clean_asic clean_fpga clean_test
	rm -rf target project generator_out.json .compile_flags

.PHONY: fpga asic vlsi test setup_% clean_%
