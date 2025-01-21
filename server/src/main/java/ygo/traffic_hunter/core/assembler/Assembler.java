package ygo.traffic_hunter.core.assembler;

public interface Assembler<REQ, RES> {

    RES assemble(REQ request);
}
