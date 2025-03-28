package org.parser.swiftdata.facade.dto;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.parser.swiftdata.infrastructure.data.SwiftCode;

@Getter
public class SwiftCodeHeadquarterResponse extends SwiftCodeBranchResponse {
    private final List<SwiftCodeBranchResponse> branches;

    public SwiftCodeHeadquarterResponse(SwiftCode swiftCode, List<SwiftCode> branches) {
        super(swiftCode);
        this.branches = branches.stream().map(SwiftCodeBranchResponse::new).collect(Collectors.toList());
    }
}
