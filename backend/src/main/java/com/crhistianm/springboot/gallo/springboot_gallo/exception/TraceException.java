package com.crhistianm.springboot.gallo.springboot_gallo.exception;


public class TraceException extends RuntimeException {

    private String sourceMethodName;
    
    public TraceException(String message, String methodPrefix) {
        super(message);
        loadMethodSourceName(methodPrefix);
    }

    private void loadMethodSourceName(String methodPrefix){
        this.sourceMethodName = StackWalker.getInstance()
            .walk(frame -> frame.map(StackWalker.StackFrame::getMethodName)
                    .filter(name -> name.contains(methodPrefix))
                    .findFirst()).orElse("method name not found");
    }

    public String getMethodSourceName() {
        return sourceMethodName;
    }

}
