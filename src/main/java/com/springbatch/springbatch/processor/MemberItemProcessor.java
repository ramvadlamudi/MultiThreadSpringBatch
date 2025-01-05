package com.springbatch.springbatch.processor;
import com.springbatch.springbatch.dto.MemberDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class MemberItemProcessor implements ItemProcessor<MemberDto, MemberDto> {

private static final Logger log = LoggerFactory.getLogger(MemberItemProcessor.class);

        @Override
        public MemberDto process(MemberDto item) throws Exception {
                // Optional: Add transformation or validation logic here if needed
                return item;
        }
}
